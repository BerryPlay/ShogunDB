package de.shogundb.domain.contributionClass;

import com.google.gson.Gson;
import de.shogundb.domain.member.MemberRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;


@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles(profiles = "test")
@AutoConfigureTestDatabase
@Transactional
public class ContributionClassControllerTests {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ContributionClassRepository contributionClassRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    public void all_contribution_classes_can_be_called() throws Exception {
        ContributionClass contributionClass1 = this.contributionClassRepository.save(
                ContributionClass.builder()
                        .name("Test Contribution Class")
                        .baseContribution(30.5)
                        .additionalContribution(5)
                        .build());

        ContributionClass contributionClass2 = this.contributionClassRepository.save(
                ContributionClass.builder()
                        .name("Another Test Contribution Class")
                        .baseContribution(20.5)
                        .additionalContribution(4.5)
                        .build());

        this.mockMvc.perform(MockMvcRequestBuilders.get("/contributionClass"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", is(contributionClass1.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].baseContribution", is(contributionClass1.getBaseContribution())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].additionalContribution", is(contributionClass1.getAdditionalContribution())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name", is(contributionClass2.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].baseContribution", is(contributionClass2.getBaseContribution())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].additionalContribution", is(contributionClass2.getAdditionalContribution())));
    }

    @Test
    public void contribution_class_can_be_added() throws Exception {
        ContributionClass contributionClass = ContributionClass.builder()
                .name("Test Contribution Class")
                .baseContribution(30.5)
                .additionalContribution(5)
                .build();

        this.mockMvc.perform(MockMvcRequestBuilders.post("/contributionClass")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(new Gson().toJson(contributionClass)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", is(contributionClass.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.baseContribution", is(contributionClass.getBaseContribution())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.additionalContribution", is(contributionClass.getAdditionalContribution())));

        assertEquals(1, this.contributionClassRepository.count());

        contributionClass.setName("");

        this.mockMvc.perform(MockMvcRequestBuilders.post("/contributionClass")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(new Gson().toJson(contributionClass)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void contribution_class_can_be_updated() throws Exception {
        ContributionClass contributionClass = this.contributionClassRepository.save(
                ContributionClass.builder()
                        .name("Test Contribution Class")
                        .baseContribution(30.5)
                        .additionalContribution(5)
                        .build());

        contributionClass.setName("New Contribution Class Name");
        contributionClass.setBaseContribution(45.32);
        contributionClass.setAdditionalContribution(10.45);

        this.mockMvc.perform(MockMvcRequestBuilders.put("/contributionClass")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(new Gson().toJson(contributionClass)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", is(contributionClass.getId().intValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", is(contributionClass.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.baseContribution", is(contributionClass.getBaseContribution())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.additionalContribution", is(contributionClass.getAdditionalContribution())));

        this.mockMvc.perform(MockMvcRequestBuilders.put("/contributionClass")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(new Gson().toJson(contributionClass)
                        .replace("\"id\":" + contributionClass.getId(), "\"id\":-1")))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    public void contribution_class_can_be_deleted() throws Exception {
        ContributionClass contributionClass = this.contributionClassRepository.save(
                ContributionClass.builder()
                        .name("Test Contribution Class")
                        .baseContribution(30.5)
                        .additionalContribution(5)
                        .build());

        this.mockMvc.perform(MockMvcRequestBuilders.delete("/contributionClass/" + contributionClass.getId()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        assertFalse(this.contributionClassRepository.findById(contributionClass.getId()).isPresent());

        this.mockMvc.perform(MockMvcRequestBuilders.delete("/contributionClass/-1"))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    public void contribution_class_can_be_called_by_id() throws Exception {
        ContributionClass contributionClass = this.contributionClassRepository.save(
                ContributionClass.builder()
                        .name("Test Contribution Class")
                        .baseContribution(30.5)
                        .additionalContribution(5)
                        .build());

        this.mockMvc.perform(MockMvcRequestBuilders.get("/contributionClass/" + contributionClass.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", is(contributionClass.getId().intValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", is(contributionClass.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.baseContribution", is(contributionClass.getBaseContribution())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.additionalContribution", is(contributionClass.getAdditionalContribution())));

        this.mockMvc.perform(MockMvcRequestBuilders.get("/contributionClass/-1"))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }
}
