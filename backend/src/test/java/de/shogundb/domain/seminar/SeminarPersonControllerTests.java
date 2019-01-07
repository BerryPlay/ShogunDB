package de.shogundb.domain.seminar;

import de.shogundb.domain.person.Person;
import de.shogundb.domain.person.PersonRepository;
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

import static de.shogundb.TestHelper.createTestPerson;
import static de.shogundb.TestHelper.createTestSeminar;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles(profiles = "test")
@AutoConfigureTestDatabase
@Transactional
public class SeminarPersonControllerTests {
    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private SeminarRepository seminarRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void all_persons_of_a_seminar_can_be_called() throws Exception {
        // add a seminar
        Seminar seminar = createTestSeminar();

        // add referents
        Person referent1 = personRepository.save(createTestPerson());
        Person referent2 = personRepository.save(createTestPerson());

        // link referents to the seminar
        seminar.getReferents().add(referent1);
        seminar.getReferents().add(referent2);
        referent1.getSeminars().add(seminar);
        referent2.getSeminars().add(seminar);

        // save everything to the database
        seminar = seminarRepository.save(seminar);

        mockMvc.perform(get("/seminar/person/" + seminar.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(is(referent1.getId().intValue())))
                .andExpect(jsonPath("$[0].name").value(is(referent1.getName())))
                .andExpect(jsonPath("$[1].id").value(is(referent2.getId().intValue())))
                .andExpect(jsonPath("$[1].name").value(is(referent2.getName())));

        mockMvc.perform(get("/seminar/person/-1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void person_can_be_added_to_seminar() throws Exception {
        // create a seminar
        Seminar seminar = seminarRepository.save(createTestSeminar());

        // create a referent
        Person referent = personRepository.save(createTestPerson());

        mockMvc.perform(post("/seminar/person/" + seminar.getId() + "/" + referent.getId()))
                .andExpect(status().isCreated());

        // try to do the same request again (should be a conflict now)
        mockMvc.perform(post("/seminar/person/" + seminar.getId() + "/" + referent.getId()))
                .andExpect(status().isConflict());

        // get the updated entities
        Seminar updatedSeminar = seminarRepository.findById(seminar.getId()).orElseThrow();
        Person updatedReferent = personRepository.findById(referent.getId()).orElseThrow();

        // check the link between the referent and the seminar
        assertEquals(1, updatedSeminar.getReferents().size());
        assertEquals(1, referent.getSeminars().size());
        assertEquals(updatedSeminar, updatedReferent.getSeminars().get(0));
        assertEquals(updatedReferent, updatedSeminar.getReferents().get(0));

        // destructive test with wrong seminar id
        mockMvc.perform(post("/seminar/person/-1/" + referent.getId()))
                .andExpect(status().isNotFound());

        // destructive test with wrong person id
        mockMvc.perform(post("/seminar/person/" + seminar.getId() + "/-1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void person_can_be_removed_from_seminar() throws Exception {
        // add a seminar
        Seminar seminar = createTestSeminar();

        // add referents
        Person referent1 = personRepository.save(createTestPerson());
        Person referent2 = personRepository.save(createTestPerson());

        // link referents to the seminar
        seminar.getReferents().add(referent1);
        seminar.getReferents().add(referent2);
        referent1.getSeminars().add(seminar);
        referent2.getSeminars().add(seminar);

        // save everything to the database
        seminar = seminarRepository.save(seminar);

        mockMvc.perform(delete("/seminar/person/" + seminar.getId() + "/" + referent1.getId()))
                .andExpect(status().isNoContent());

        // update the previous entities
        seminar = this.seminarRepository.findById(seminar.getId()).orElseThrow();
        referent1 = this.personRepository.findById(referent1.getId()).orElseThrow();
        referent2 = this.personRepository.findById(referent2.getId()).orElseThrow();

        assertEquals(1, seminar.getReferents().size());
        assertEquals(referent2, seminar.getReferents().get(0));
        assertFalse(seminar.getReferents().contains(referent1));

        assertEquals(0, referent1.getSeminars().size());
    }
}
