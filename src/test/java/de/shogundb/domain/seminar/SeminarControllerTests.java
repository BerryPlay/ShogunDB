package de.shogundb.domain.seminar;

import com.google.gson.Gson;
import de.shogundb.TestHelper;
import de.shogundb.domain.contributionClass.ContributionClassRepository;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.ArrayList;

import static de.shogundb.TestHelper.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void all_seminars_can_be_called() throws Exception {
        Seminar seminar1 = seminarRepository.save(createTestSeminar());

        Seminar seminar2 = seminarRepository.save(createTestSeminar());

        mockMvc.perform(MockMvcRequestBuilders.get("/seminar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(hasSize(2)))

                // test values of seminar 1
                .andExpect(jsonPath("$[0].id").value(is(seminar1.getId().intValue())))
                .andExpect(jsonPath("$[0].name").value(is(seminar1.getName())))
                .andExpect(jsonPath("$[0].place").value(is(seminar1.getPlace())))
                .andExpect(jsonPath("$[0].seminarType",
                        is(seminar1.getSeminarType().toString())))
                .andExpect(jsonPath("$[0].dateFrom",
                        is(seminar1.getDateFrom().toString())))
                .andExpect(jsonPath("$[0].dateTo",
                        is(seminar1.getDateTo().toString())))

                // test values of seminar 2
                .andExpect(jsonPath("$[1].id").value(is(seminar2.getId().intValue())))
                .andExpect(jsonPath("$[1].name").value(is(seminar2.getName())))
                .andExpect(jsonPath("$[1].place").value(is(seminar2.getPlace())))
                .andExpect(jsonPath("$[1].seminarType",
                        is(seminar2.getSeminarType().toString())))
                .andExpect(jsonPath("$[1].dateFrom",
                        is(seminar2.getDateFrom().toString())))
                .andExpect(jsonPath("$[1].dateTo",
                        is(seminar2.getDateTo().toString())));
    }

    @Test
    public void seminar_can_be_added() throws Exception {
        Member member1 = memberRepository.save(TestHelper.createTestMember(contributionClassRepository));
        Member member2 = memberRepository.save(TestHelper.createTestMember(contributionClassRepository));
        Person referent = personRepository.save(Person.builder()
                .name("Test Person")
                .build());

        SeminarRegisterDTO seminar = SeminarRegisterDTO.builder()
                .name("Test Seminar")
                .place("Test Place")
                .seminarType(SeminarType.NATIONAL)
                .dateFrom(LocalDate.parse("2018-01-02"))
                .dateTo(LocalDate.parse("2018-01-02"))
                .members(new ArrayList<Long>() {{
                    add(member1.getId());
                    add(member2.getId());
                }})
                .referents(new ArrayList<Long>() {{
                    add(referent.getId());
                }})
                .build();

        mockMvc.perform(post("/seminar")
                .contentType(APPLICATION_JSON_UTF8)
                .content(toJson(seminar)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(is(seminar.getName())))
                .andExpect(jsonPath("$.place").value(is(seminar.getPlace())))
                .andExpect(jsonPath("$.seminarType").value(is(seminar.getSeminarType().toString())))
                .andExpect(jsonPath("$.dateFrom").value(is(seminar.getDateFrom().toString())))
                .andExpect(jsonPath("$.dateTo").value(is(seminar.getDateTo().toString())));

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
        Member member1 = memberRepository.save(TestHelper.createTestMember(contributionClassRepository));
        Member member2 = memberRepository.save(TestHelper.createTestMember(contributionClassRepository));
        Member member3 = memberRepository.save(TestHelper.createTestMember(contributionClassRepository));

        // add two referents to the database
        Person referent1 = personRepository.save(createTestPerson());
        Person referent2 = personRepository.save(createTestPerson());

        // add a seminar to the database
        Seminar seminar = createTestSeminar();

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
                .dateFrom(LocalDate.parse("2018-01-02"))
                .dateTo(LocalDate.parse("2018-01-02"))
                .seminarType(SeminarType.GLOBAL)
                .members(new ArrayList<Long>() {{
                    add(member1.getId());
                    add(member3.getId());
                }})
                .referents(new ArrayList<Long>() {{
                    add(referent2.getId());
                }})
                .build();

        mockMvc.perform(put("/seminar")
                .contentType(APPLICATION_JSON_UTF8)
                .content(toJson(seminarUpdateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(is(seminarUpdateDTO.getId().intValue())))
                .andExpect(jsonPath("$.name").value(is(seminarUpdateDTO.getName())))
                .andExpect(jsonPath("$.place").value(is(seminarUpdateDTO.getPlace())))
                .andExpect(jsonPath("$.dateFrom").value(is(seminarUpdateDTO.getDateFrom().toString())))
                .andExpect(jsonPath("$.dateTo").value(is(seminarUpdateDTO.getDateTo().toString())))
                .andExpect(jsonPath("$.seminarType").value(is(seminarUpdateDTO.getSeminarType().toString())));

        // update the previous entities
        Seminar updatedSeminar = seminarRepository.findById(seminar.getId()).orElseThrow();
        Member updatedMember1 = memberRepository.findById(member1.getId()).orElseThrow();
        Member updatedMember2 = memberRepository.findById(member2.getId()).orElseThrow();
        Member updatedMember3 = memberRepository.findById(member3.getId()).orElseThrow();
        Person updatedReferent1 = personRepository.findById(referent1.getId()).orElseThrow();
        Person updatedReferent2 = personRepository.findById(referent2.getId()).orElseThrow();

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

    @Test
    public void seminar_can_be_deleted() throws Exception {
        // add three member to the database
        Member member1 = memberRepository.save(TestHelper.createTestMember(contributionClassRepository));
        Member member2 = memberRepository.save(TestHelper.createTestMember(contributionClassRepository));

        // add two referents to the database
        Person referent1 = personRepository.save(createTestPerson());

        // create a seminar
        Seminar seminar = createTestSeminar();

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

        mockMvc.perform(delete("/seminar/" + seminar.getId().intValue()))
                .andExpect(status().isNoContent());

        // check, if the seminar was removed from the database
        assertFalse(seminarRepository.findById(seminar.getId()).isPresent());

        // update the previous entities
        Member updatedMember1 = memberRepository.findById(member1.getId()).orElseThrow();
        Member updatedMember2 = memberRepository.findById(member2.getId()).orElseThrow();
        Person updatedReferent1 = personRepository.findById(referent1.getId()).orElseThrow();

        /*
         * Test the links between the members/referents and the seminar
         */
        assertEquals(0, updatedMember1.getSeminars().size());
        assertEquals(0, updatedMember2.getSeminars().size());
        assertEquals(0, updatedReferent1.getSeminars().size());
    }

    @Test
    public void seminar_can_be_called_by_id() throws Exception {
        // add a seminar to the database
        Seminar seminar = seminarRepository.save(createTestSeminar());

        mockMvc.perform(get("/seminar/" + seminar.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(seminar.getId().intValue()))
                .andExpect(jsonPath("$.name").value(seminar.getName()))
                .andExpect(jsonPath("$.place").value(seminar.getPlace()))
                .andExpect(jsonPath("$.dateFrom").value(seminar.getDateFrom().toString()))
                .andExpect(jsonPath("$.dateTo").value(seminar.getDateTo().toString()))
                .andExpect(jsonPath("$.seminarType").value(seminar.getSeminarType().toString()));

        // destructive test with non existing id
        mockMvc.perform(get("/seminar/-1"))
                .andExpect(status().isNotFound());
    }
}
