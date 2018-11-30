package de.shogundb.domain.person;

import de.shogundb.TestHelper;
import de.shogundb.domain.seminar.Seminar;
import de.shogundb.domain.seminar.SeminarRepository;
import de.shogundb.domain.seminar.SeminarType;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles(profiles = "test")
@AutoConfigureTestDatabase
@Transactional
public class PersonControllerTests {
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
    public void all_persons_can_be_called() throws Exception {
        Seminar seminar = seminarRepository.save(Seminar.builder()
                .name("Test Seminar")
                .place("Test Place")
                .seminarType(SeminarType.NATIONAL)
                .dateTo(LocalDate.parse("2018-01-02"))
                .dateFrom(LocalDate.parse("2018-01-02"))
                .build());

        Person person = personRepository.save(Person.builder()
                .name("Test Person")
                .build());

        seminar.getReferents().add(person);
        person.getSeminars().add(seminar);

        person = personRepository.save(person);

        mockMvc.perform(get("/person"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(is(person.getId().intValue())))
                .andExpect(jsonPath("$[0].name").value(is(person.getName())))
                .andExpect(jsonPath("$[0].seminars").value(hasSize(1)))
                .andExpect(jsonPath("$[0].seminars[0].id").value(is(seminar.getId().intValue())));
    }

    @Test
    public void person_can_be_added() throws Exception {
        Person person = Person.builder()
                .name("Test Person")
                .build();

        mockMvc.perform(post("/person")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(TestHelper.toJson(person)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(notNullValue()))
                .andExpect(jsonPath("$.name").value(is(person.getName())));

        mockMvc.perform(post("/person")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(TestHelper.toJson(person)))
                .andExpect(status().isConflict());

        Person newPerson = personRepository.findByNameEquals(person.getName())
                .orElseThrow(PersonNotFoundException::new);
        assertEquals(person.getName(), newPerson.getName());
    }
}
