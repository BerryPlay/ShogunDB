package de.shogundb.domain.seminar;

import com.google.gson.*;
import de.shogundb.domain.contributionClass.ContributionClass;
import de.shogundb.domain.contributionClass.ContributionClassRepository;
import de.shogundb.domain.member.Gender;
import de.shogundb.domain.member.Member;
import de.shogundb.domain.member.MemberNotFoundException;
import de.shogundb.domain.member.MemberRepository;
import de.shogundb.domain.person.Person;
import de.shogundb.domain.person.PersonRepository;
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

import java.util.ArrayList;
import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles(profiles = "test")
@AutoConfigureTestDatabase
@Transactional
public class SeminarControllerTests {
    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private SeminarRepository seminarRepository;

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
    public void all_seminars_can_be_called() throws Exception {
        Seminar seminar1 = this.seminarRepository.save(Seminar.builder()
                .name("Test Seminar")
                .place("Test Place")
                .seminarType(SeminarType.NATIONAL)
                .dateTo(new Date(1515283200000L))
                .dateFrom(new Date(1515196800000L))
                .build());

        Seminar seminar2 = this.seminarRepository.save(Seminar.builder()
                .name("Another Test Seminar")
                .place("Another Test Place")
                .seminarType(SeminarType.NATIONAL)
                .dateTo(new Date(1515283200000L))
                .dateFrom(new Date(1515196800000L))
                .build());

        mockMvc.perform(MockMvcRequestBuilders.get("/seminar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))

                // test values of seminar 1
                .andExpect(jsonPath("$[0].id", is(seminar1.getId().intValue())))
                .andExpect(jsonPath("$[0].name", is(seminar1.getName())))
                .andExpect(jsonPath("$[0].place", is(seminar1.getPlace())))
                .andExpect(jsonPath("$[0].seminarType",
                        is(seminar1.getSeminarType().toString())))
                .andExpect(jsonPath("$[0].dateFrom",
                        is(seminar1.getDateFrom().getTime())))
                .andExpect(jsonPath("$[0].dateTo",
                        is(seminar1.getDateTo().getTime())))

                // test values of seminar 2
                .andExpect(jsonPath("$[1].id", is(seminar2.getId().intValue())))
                .andExpect(jsonPath("$[1].name", is(seminar2.getName())))
                .andExpect(jsonPath("$[1].place", is(seminar2.getPlace())))
                .andExpect(jsonPath("$[1].seminarType",
                        is(seminar2.getSeminarType().toString())))
                .andExpect(jsonPath("$[1].dateFrom",
                        is(seminar2.getDateFrom().getTime())))
                .andExpect(jsonPath("$[1].dateTo",
                        is(seminar2.getDateTo().getTime())));
    }

    @Test
    public void seminar_can_be_added() throws Exception {
        Member member1 = memberRepository.save(createTestMember());
        Member member2 = memberRepository.save(createTestMember());
        Person referent = personRepository.save(Person.builder()
                .name("Test Person")
                .build());

        SeminarRegisterDTO seminar = SeminarRegisterDTO.builder()
                .name("Test Seminar")
                .place("Test Place")
                .seminarType(SeminarType.NATIONAL)
                .dateFrom(new Date(1515196800000L))
                .dateTo(new Date(1515283200000L))
                .members(new ArrayList<Long>() {{
                    add(member1.getId());
                    add(member2.getId());
                }})
                .referents(new ArrayList<Long>() {{
                    add(referent.getId());
                }})
                .build();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json, typeOfT, context)
                        -> new Date(json.getAsJsonPrimitive().getAsLong()))
                .registerTypeAdapter(Date.class, (JsonSerializer<Date>) (date, type, jsonSerializationContext)
                        -> new JsonPrimitive(date.getTime()))
                .create();

        mockMvc.perform(post("/seminar")
                .contentType(APPLICATION_JSON_UTF8)
                .content(gson.toJson(seminar)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(seminar.getName())))
                .andExpect(jsonPath("$.place", is(seminar.getPlace())))
                .andExpect(jsonPath("$.seminarType", is(seminar.getSeminarType().toString())))
                .andExpect(jsonPath("$.dateFrom", is(seminar.getDateFrom().getTime())))
                .andExpect(jsonPath("$.dateTo", is(seminar.getDateTo().getTime())));

        // get the added seminar from the database
        Seminar newSeminar = seminarRepository.findAll().iterator().next();

        Member updatedMember1 = memberRepository.findById(member1.getId()).orElseThrow(MemberNotFoundException::new);
        Member updatedMember2 = memberRepository.findById(member2.getId()).orElseThrow(MemberNotFoundException::new);

        // assert, if the new seminar is linked to the members
        assertEquals(newSeminar.getId(), updatedMember1.getSeminars().iterator().next().getId());
        assertEquals(newSeminar.getId(), updatedMember2.getSeminars().iterator().next().getId());

        // assert, if the members are linked to the new seminar
        assertEquals(updatedMember1.getId(), newSeminar.getMembers().get(0).getId());
        assertEquals(updatedMember2.getId(), newSeminar.getMembers().get(1).getId());

        // destructive test (wrong date format)
        mockMvc.perform(post("/seminar")
                .contentType(APPLICATION_JSON_UTF8)
                .content(new Gson().toJson(seminar)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void seminar_can_be_updated() throws Exception {
        // add three member to the database
        Member member1 = memberRepository.save(createTestMember());
        Member member2 = memberRepository.save(createTestMember());
        Member member3 = memberRepository.save(createTestMember());

        // add two referents to the database
        Person referent1 = personRepository.save(Person.builder()
                .name("Test Person")
                .build());
        Person referent2 = personRepository.save(Person.builder()
                .name("Another Test Person")
                .build());

        // add a seminar to the database
        Seminar seminar = Seminar.builder()
                .name("Test Seminar")
                .place("Test Place")
                .seminarType(SeminarType.LOCAL)
                .dateFrom(new Date(1515196800000L))
                .dateTo(new Date(1515283200000L))
                .build();

        // link the members and the seminar
        seminar.getMembers().add(member1);
        seminar.getMembers().add(member2);
        member1.getSeminars().add(seminar);
        member2.getSeminars().add(seminar);

        // line the referent to the seminar
        seminar.getReferents().add(referent1);
        referent1.getSeminars().add(seminar);

        // save everything to the database
        seminar = seminarRepository.save(seminar);

        // create a seminar data transfer object to update the existing seminar
        SeminarUpdateDTO seminarUpdateDTO = SeminarUpdateDTO.builder()
                .id(seminar.getId())
                .name("Updated Name")
                .place("New Place")
                .dateFrom(new Date(1515196807000L))
                .dateTo(new Date(1215196807000L))
                .seminarType(SeminarType.GLOBAL)
                .members(new ArrayList<Long>() {{
                    add(member1.getId());
                    add(member3.getId());
                }})
                .referents(new ArrayList<Long>() {{
                    add(referent2.getId());
                }})
                .build();

        // create a gson object to convert the dto to a json string
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json, typeOfT, context)
                        -> new Date(json.getAsJsonPrimitive().getAsLong()))
                .registerTypeAdapter(Date.class, (JsonSerializer<Date>) (date, type, jsonSerializationContext)
                        -> new JsonPrimitive(date.getTime()))
                .create();

        mockMvc.perform(put("/seminar")
                .contentType(APPLICATION_JSON_UTF8)
                .content(gson.toJson(seminarUpdateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(is(seminarUpdateDTO.getId().intValue())))
                .andExpect(jsonPath("$.name").value(is(seminarUpdateDTO.getName())))
                .andExpect(jsonPath("$.place").value(is(seminarUpdateDTO.getPlace())))
                .andExpect(jsonPath("$.dateFrom").value(is(seminarUpdateDTO.getDateFrom().getTime())))
                .andExpect(jsonPath("$.dateTo").value(is(seminarUpdateDTO.getDateTo().getTime())))
                .andExpect(jsonPath("$.seminarType").value(is(seminarUpdateDTO.getSeminarType().toString())));

        // update the previous entities
        Seminar updatedSeminar = seminarRepository.findOne(seminar.getId());
        Member updatedMember1 = memberRepository.findOne(member1.getId());
        Member updatedMember2 = memberRepository.findOne(member2.getId());
        Member updatedMember3 = memberRepository.findOne(member3.getId());
        Person updatedReferent1 = personRepository.findOne(referent1.getId());
        Person updatedReferent2 = personRepository.findOne(referent2.getId());

        /*
         * Test the links between the members and the seminar
         */
        assertEquals(2, updatedSeminar.getMembers().size());

        assertTrue(updatedSeminar.getMembers().contains(updatedMember1));
        assertFalse(updatedSeminar.getMembers().contains(updatedMember2));
        assertTrue(updatedSeminar.getMembers().contains(updatedMember3));

        assertEquals(1, updatedMember1.getSeminars().size());
        assertEquals(0, updatedMember2.getSeminars().size());
        assertEquals(1, updatedMember3.getSeminars().size());

        assertEquals(updatedSeminar, updatedMember1.getSeminars().get(0));
        assertEquals(updatedSeminar, updatedMember3.getSeminars().get(0));

        /*
         * Test the links between the referents and the seminar
         */
        assertFalse(updatedSeminar.getReferents().contains(updatedReferent1));
        assertTrue(updatedSeminar.getReferents().contains(updatedReferent2));
    }

    private Member createTestMember() {
        return Member.builder()
                .forename("Max")
                .surname("Mustermann")
                .gender(Gender.MALE)
                .street("Musterstraße")
                .postcode("26721")
                .phoneNumber("04929 5435438")
                .mobileNumber("1522 416845575")
                .email("max@muster.de")
                .dateOfBirth(new Date(810086400000L))
                .hasBudoPass(true)
                .budoPassDate(new Date(1514764800000L))
                .enteredDate(new Date(1514764800000L))
                .hasLeft(false)
                .leftDate(null)
                .isPassive(false)
                .contributionClass(
                        this.contributionClassRepository.save(
                                ContributionClass.builder()
                                        .name("Test")
                                        .baseContribution(27.7)
                                        .additionalContribution(5)
                                        .build()))
                .accountHolder("Max Mustermann")
                .build();
    }
}
