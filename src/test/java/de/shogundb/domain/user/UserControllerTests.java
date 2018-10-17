package de.shogundb.domain.user;

import de.shogundb.TestHelper;
import de.shogundb.domain.token.Token;
import de.shogundb.domain.token.TokenService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.NestedServletException;

import java.util.ArrayList;

import static junit.framework.TestCase.fail;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles(profiles = "test")
@AutoConfigureTestDatabase
@Transactional
public class UserControllerTests {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private TokenService tokenService;


    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    public void all_users_can_be_called() throws Exception {
        // insert a test user
        User user = this.userRepository.save(
                User.builder()
                        .username("testuser")
                        .password(BCrypt.hashpw("testpass", BCrypt.gensalt()))
                        .email("test@email.com")
                        .token(new ArrayList<>())
                        .build());

        this.mockMvc.perform(get("/user"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(user.getId().intValue())))
                .andExpect(jsonPath("$[0].username", is(user.getUsername())))
                .andExpect(jsonPath("$[0].email", is(user.getEmail())));

        // insert another test user
        User user2 = userRepository.save(
                User.builder()
                        .username("test2")
                        .password(BCrypt.hashpw("testpass2", BCrypt.gensalt()))
                        .email("test2@email.com")
                        .token(new ArrayList<>())
                        .build());

        mockMvc.perform(get("/user"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[1].id", is(user2.getId().intValue())))
                .andExpect(jsonPath("$[1].username", is(user2.getUsername())))
                .andExpect(jsonPath("$[1].email", is(user2.getEmail())));
    }

    @Test
    public void can_check_if_user_exists() throws Exception {
        // insert a test user
        User user = this.userRepository.save(
                User.builder()
                        .username("testuser")
                        .password(BCrypt.hashpw("testpass", BCrypt.gensalt()))
                        .email("test@email.com")
                        .token(new ArrayList<>())
                        .build());

        // with existing user
        mockMvc.perform(head("/user/" + user.getId()))
                .andExpect(status().isOk());

        // with a non existing user
        mockMvc.perform(head("/user/-1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void user_can_be_called_by_id() throws Exception {
        // insert a test user
        User user = this.userRepository.save(
                User.builder()
                        .username("test")
                        .password(BCrypt.hashpw("test", BCrypt.gensalt()))
                        .email("test@email.com")
                        .token(new ArrayList<>())
                        .build());

        mockMvc.perform(get("/user/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));

        // test with a non existing user
        mockMvc.perform(get("/user/-1"))
                .andExpect(status().isConflict());
    }

    @Test
    public void user_can_be_added() throws Exception {
        // insert a test user
        User user = User.builder()
                .username("testuser2")
                .password("testpass")
                .email("test@email.com")
                .build();

        String userJsonString = TestHelper.toJson(user);

        mockMvc.perform(
                post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJsonString))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.email", is(user.getEmail())))
                .andReturn();

        // test, if validation works
        user.setPassword(null);
        mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestHelper.toJson(user)))
                .andExpect(status().isBadRequest());
    }

    public void user_can_be_updated() throws Exception {
        // insert a test user
        User user = userRepository.save(
                User.builder()
                        .username("test")
                        .password(BCrypt.hashpw("testpass", BCrypt.gensalt()))
                        .email("test@email.com")
                        .build());

        user.setEmail("new.email@internet.com");
        user.setPassword("new_super_secure_password");

        String userJsonString = TestHelper.toJson(user);

        mockMvc.perform(
                put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJsonString))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));

        User updatedUser = this.userRepository.findById(user.getId())
                .orElseThrow(UserNotFoundException::new);
        assertEquals(updatedUser.getPassword(), user.getPassword());

        // with a not existing user
        User newUser = User.builder()
                .username("this")
                .password(BCrypt.hashpw("user_does_not", BCrypt.gensalt()))
                .email("exist-in.the@database.de")
                .build();

        newUser.setId(0L);
        userJsonString = TestHelper.toJson(newUser);
        try {
            mockMvc.perform(put("/user")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(userJsonString));
            fail();
        } catch (NestedServletException e) {
            assertEquals(UserNotFoundException.class, e.getCause().getClass());
        }
    }

    @Test
    public void user_can_be_removed() throws Exception {
        // insert a test user
        User user = userRepository.save(
                User.builder()
                        .username("test")
                        .password(BCrypt.hashpw("testpass", BCrypt.gensalt()))
                        .email("test@email.com")
                        .build());

        // generate a authentication token
        Token token = this.tokenService.generate(user);

        mockMvc.perform(delete("/user/" + user.getId())
                .header("Authorization", "Bearer " + token.getToken()))
                .andExpect(status().isNoContent());

        assertFalse(this.userRepository.findById(user.getId()).isPresent());
    }
}

