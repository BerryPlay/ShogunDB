package de.shogundb.domain.contributionClass;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static de.shogundb.TestHelper.toJson;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles(profiles = "test")
@AutoConfigureTestDatabase
@Transactional
public class ContributionClassControllerTests {
    @Autowired
    private ContributionClassRepository contributionClassRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void all_contribution_classes_can_be_called() throws Exception {
        ContributionClass contributionClass1 = contributionClassRepository.save(
                ContributionClass.builder()
                        .name("Test Contribution Class")
                        .baseContribution(30.5)
                        .additionalContribution(5)
                        .build());

        ContributionClass contributionClass2 = contributionClassRepository.save(
                ContributionClass.builder()
                        .name("Another Test Contribution Class")
                        .baseContribution(20.5)
                        .additionalContribution(4.5)
                        .build());

        mockMvc.perform(get("/contributionClass"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(hasSize(2)))
                .andExpect(jsonPath("$[0].name")
                        .value(is(contributionClass1.getName())))
                .andExpect(jsonPath("$[0].baseContribution")
                        .value(is(contributionClass1.getBaseContribution())))
                .andExpect(jsonPath("$[0].additionalContribution")
                        .value(is(contributionClass1.getAdditionalContribution())))
                .andExpect(jsonPath("$[1].name").value(is(contributionClass2.getName())))
                .andExpect(jsonPath("$[1].baseContribution")
                        .value(is(contributionClass2.getBaseContribution())))
                .andExpect(jsonPath("$[1].additionalContribution")
                        .value(is(contributionClass2.getAdditionalContribution())));
    }

    @Test
    public void contribution_class_can_be_added() throws Exception {
        ContributionClass contributionClass = ContributionClass.builder()
                .name("Test Contribution Class")
                .baseContribution(30.5)
                .additionalContribution(5)
                .build();

        mockMvc.perform(post("/contributionClass")
                .contentType(APPLICATION_JSON_UTF8)
                .content(toJson(contributionClass)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(notNullValue()))
                .andExpect(jsonPath("$.name").value(is(contributionClass.getName())))
                .andExpect(jsonPath("$.baseContribution").value(is(contributionClass.getBaseContribution())))
                .andExpect(jsonPath("$.additionalContribution")
                        .value(is(contributionClass.getAdditionalContribution())));

        assertEquals(1, contributionClassRepository.count());

        contributionClass.setName("");

        mockMvc.perform(post("/contributionClass")
                .contentType(APPLICATION_JSON_UTF8)
                .content(toJson(contributionClass)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void contribution_class_can_be_updated() throws Exception {
        ContributionClass contributionClass = contributionClassRepository.save(
                ContributionClass.builder()
                        .name("Test Contribution Class")
                        .baseContribution(30.5)
                        .additionalContribution(5)
                        .build());

        contributionClass.setName("New Contribution Class Name");
        contributionClass.setBaseContribution(45.32);
        contributionClass.setAdditionalContribution(10.45);

        mockMvc.perform(put("/contributionClass")
                .contentType(APPLICATION_JSON_UTF8)
                .content(toJson(contributionClass)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(is(contributionClass.getId().intValue())))
                .andExpect(jsonPath("$.name").value(is(contributionClass.getName())))
                .andExpect(jsonPath("$.baseContribution").value(is(contributionClass.getBaseContribution())))
                .andExpect(jsonPath("$.additionalContribution")
                        .value(is(contributionClass.getAdditionalContribution())));

        mockMvc.perform(put("/contributionClass")
                .contentType(APPLICATION_JSON_UTF8)
                .content(toJson(contributionClass)
                        .replace("\"id\":" + contributionClass.getId(), "\"id\":-1")))
                .andExpect(status().isConflict());
    }

    @Test
    public void contribution_class_can_be_deleted() throws Exception {
        ContributionClass contributionClass = contributionClassRepository.save(
                ContributionClass.builder()
                        .name("Test Contribution Class")
                        .baseContribution(30.5)
                        .additionalContribution(5)
                        .build());

        mockMvc.perform(delete("/contributionClass/" + contributionClass.getId()))
                .andExpect(status().isNoContent());

        assertFalse(contributionClassRepository.findById(contributionClass.getId()).isPresent());

        mockMvc.perform(delete("/contributionClass/-1"))
                .andExpect(status().isConflict());
    }

    @Test
    public void contribution_class_can_be_called_by_id() throws Exception {
        ContributionClass contributionClass = contributionClassRepository.save(
                ContributionClass.builder()
                        .name("Test Contribution Class")
                        .baseContribution(30.5)
                        .additionalContribution(5)
                        .build());

        mockMvc.perform(get("/contributionClass/" + contributionClass.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(is(contributionClass.getId().intValue())))
                .andExpect(jsonPath("$.name").value(is(contributionClass.getName())))
                .andExpect(jsonPath("$.baseContribution").value(is(contributionClass.getBaseContribution())))
                .andExpect(jsonPath("$.additionalContribution")
                        .value(is(contributionClass.getAdditionalContribution())));

        mockMvc.perform(get("/contributionClass/-1"))
                .andExpect(status().isConflict());
    }
}
