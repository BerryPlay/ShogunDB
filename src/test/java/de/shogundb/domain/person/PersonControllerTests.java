package de.shogundb.domain.person;

import com.google.gson.*;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertEquals;

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
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    public void all_persons_can_be_called() throws Exception {
        Seminar seminar = this.seminarRepository.save(Seminar.builder()
                .name("Test Seminar")
                .place("Test Place")
                .seminarType(SeminarType.NATIONAL)
                .dateTo(new Date(1515283200000L))
                .dateFrom(new Date(1515196800000L))
                .build());

        Person person = this.personRepository.save(Person.builder()
                .name("Test Person")
                .build());

        seminar.getReferents().add(person);
        person.getSeminars().add(seminar);

        person = this.personRepository.save(person);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/person"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", is(person.getId().intValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", is(person.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].seminars", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].seminars[0].id", is(seminar.getId().intValue())));
    }

    @Test
    public void person_can_be_added() throws Exception {
        Person person = Person.builder()
                .name("Test Person")
                .build();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json, typeOfT, context) -> new Date(json.getAsJsonPrimitive().getAsLong()))
                .registerTypeAdapter(Date.class, (JsonSerializer<Date>) (date, type, jsonSerializationContext) -> new JsonPrimitive(date.getTime()))
                .create();

        this.mockMvc.perform(MockMvcRequestBuilders.post("/person")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(gson.toJson(person)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", is(person.getName())));

        this.mockMvc.perform(MockMvcRequestBuilders.post("/person")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(gson.toJson(person)))
                .andExpect(MockMvcResultMatchers.status().isConflict());

        Person newPerson = this.personRepository.findByNameEquals(person.getName()).orElseThrow(PersonNotFoundException::new);
        assertEquals(person.getName(), newPerson.getName());
    }
}
