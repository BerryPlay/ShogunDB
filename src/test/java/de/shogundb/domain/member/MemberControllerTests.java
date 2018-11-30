package de.shogundb.domain.member;

import de.shogundb.TestHelper;
import de.shogundb.domain.contributionClass.ContributionClass;
import de.shogundb.domain.contributionClass.ContributionClassNotFoundException;
import de.shogundb.domain.contributionClass.ContributionClassRepository;
import de.shogundb.domain.discipline.Discipline;
import de.shogundb.domain.discipline.DisciplineNotFoundException;
import de.shogundb.domain.discipline.DisciplineRepository;
import de.shogundb.domain.event.Event;
import de.shogundb.domain.event.EventNotFoundException;
import de.shogundb.domain.event.EventRepository;
import de.shogundb.domain.seminar.Seminar;
import de.shogundb.domain.seminar.SeminarNotFoundException;
import de.shogundb.domain.seminar.SeminarRepository;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static de.shogundb.TestHelper.createTestMember;
import static de.shogundb.TestHelper.createTestSeminar;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles(profiles = "test")
@AutoConfigureTestDatabase
@Transactional
public class MemberControllerTests {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ContributionClassRepository contributionClassRepository;

    @Autowired
    private DisciplineRepository disciplineRepository;

    @Autowired
    private EventRepository eventRepository;

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
    public void all_members_can_be_called() throws Exception {
        // insert a test member
        Member member = memberRepository.save(createTestMember(contributionClassRepository));

        System.out.println("Member as JSON: " + member.getDateOfBirth());

        mockMvc.perform(get("/member"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].forename").value(is(member.getForename())))
                .andExpect(jsonPath("$[0].surname").value(is(member.getSurname())))
                .andExpect(jsonPath("$[0].gender").value(is(member.getGender().toString())))
                .andExpect(jsonPath("$[0].street").value(is(member.getStreet())))
                .andExpect(jsonPath("$[0].postcode").value(is(member.getPostcode())))
                .andExpect(jsonPath("$[0].phoneNumber").value(is(member.getPhoneNumber())))
                .andExpect(jsonPath("$[0].mobileNumber").value(is(member.getMobileNumber())))
                .andExpect(jsonPath("$[0].email").value(is(member.getEmail())))
                .andExpect(jsonPath("$[0].dateOfBirth").value(is(member.getDateOfBirth().toString())))
                .andExpect(jsonPath("$[0].hasBudoPass").value(is(member.getHasBudoPass())))
                .andExpect(jsonPath("$[0].budoPassDate").value(is(member.getBudoPassDate().toString())))
                .andExpect(jsonPath("$[0].enteredDate").value(is(member.getEnteredDate().toString())))
                .andExpect(jsonPath("$[0].hasLeft").value(is(member.getHasLeft())))
                .andExpect(jsonPath("$[0].leftDate").value(is(member.getLeftDate().toString())))
                .andExpect(jsonPath("$[0].isPassive").value(is(member.getIsPassive())))
                .andExpect(jsonPath("$[0].accountHolder").value(is(member.getAccountHolder())))
                .andExpect(jsonPath("$[0].disciplines").value(is(member.getDisciplines())));
    }

    @Test
    public void member_can_be_added() throws Exception {
        ContributionClass contributionClass = contributionClassRepository.save(
                ContributionClass.builder()
                        .name("Test")
                        .baseContribution(27.7)
                        .additionalContribution(5)
                        .build());

        Discipline discipline1 = disciplineRepository.save(Discipline.builder()
                .name("Test Discipline")
                .build());

        Discipline discipline2 = disciplineRepository.save(Discipline.builder()
                .name("Another Test Discipline")
                .build());

        List<Long> disciplines = new ArrayList<>();
        disciplines.add(discipline1.getId());
        disciplines.add(discipline2.getId());

        // test member
        MemberRegisterDTO memberRegisterDTO = MemberRegisterDTO.builder()
                .forename("Max")
                .surname("Mustermann")
                .gender(Gender.MALE)
                .street("Musterstraße")
                .postcode("26721")
                .phoneNumber("04929 5435438")
                .mobileNumber("1522 416845575")
                .email("max@muster.de")
                .dateOfBirth(LocalDate.parse("2018-01-02"))
                .hasBudoPass(true)
                .budoPassDate(LocalDate.parse("2018-01-02"))
                .enteredDate(LocalDate.parse("2018-01-02"))
                .hasLeft(true)
                .leftDate(LocalDate.parse("2018-01-03"))
                .isPassive(false)
                .contributionClass(contributionClass.getId())
                .accountHolder("Max Mustermann")
                .disciplines(disciplines)
                .build();

        String memberJson = TestHelper.toJson(memberRegisterDTO);

        mockMvc.perform(post("/member")
                .contentType(APPLICATION_JSON_UTF8)
                .content(memberJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.forename").value(is(memberRegisterDTO.getForename())))
                .andExpect(jsonPath("$.surname").value(is(memberRegisterDTO.getSurname())))
                .andExpect(jsonPath("$.gender").value(is(memberRegisterDTO.getGender().toString())))
                .andExpect(jsonPath("$.street").value(is(memberRegisterDTO.getStreet())))
                .andExpect(jsonPath("$.postcode").value(is(memberRegisterDTO.getPostcode())))
                .andExpect(jsonPath("$.phoneNumber").value(is(memberRegisterDTO.getPhoneNumber())))
                .andExpect(jsonPath("$.mobileNumber").value(is(memberRegisterDTO.getMobileNumber())))
                .andExpect(jsonPath("$.email").value(is(memberRegisterDTO.getEmail())))
                .andExpect(jsonPath("$.dateOfBirth").value(is(memberRegisterDTO.getDateOfBirth().toString())))
                .andExpect(jsonPath("$.hasBudoPass").value(is(memberRegisterDTO.getHasBudoPass())))
                .andExpect(jsonPath("$.budoPassDate").value(is(memberRegisterDTO.getBudoPassDate().toString())))
                .andExpect(jsonPath("$.enteredDate").value(is(memberRegisterDTO.getEnteredDate().toString())))
                .andExpect(jsonPath("$.hasLeft").value(is(memberRegisterDTO.getHasLeft())))
                .andExpect(jsonPath("$.leftDate").value(is(memberRegisterDTO.getLeftDate().toString())))
                .andExpect(jsonPath("$.isPassive").value(is(memberRegisterDTO.getIsPassive())))
                .andExpect(jsonPath("$.accountHolder").value(is(memberRegisterDTO.getAccountHolder())));

        final ContributionClass finalContributionClass = contributionClassRepository.findById(contributionClass.getId())
                .orElseThrow(ContributionClassNotFoundException::new);

        final Discipline finalDiscipline1 = disciplineRepository.findById(discipline1.getId())
                .orElseThrow(DisciplineNotFoundException::new);

        final Discipline finalDiscipline2 = disciplineRepository.findById(discipline2.getId())
                .orElseThrow(DisciplineNotFoundException::new);

        assertEquals(1, memberRepository.count());

        memberRepository.findAll().forEach(
                existing -> {
                    // contribution class
                    assertEquals(finalContributionClass, existing.getContributionClass());
                    assertEquals(1, finalContributionClass.getMembers().size());
                    assertTrue(finalContributionClass.getMembers().contains(existing));

                    // disciplines
                    assertEquals(2, existing.getDisciplines().size());
                    assertTrue(existing.getDisciplines().contains(finalDiscipline1));
                    assertTrue(existing.getDisciplines().contains(finalDiscipline2));
                    assertEquals(1, finalDiscipline1.getMembers().size());
                    assertEquals(1, finalDiscipline2.getMembers().size());
                    assertTrue(finalDiscipline1.getMembers().contains(existing));
                    assertTrue(finalDiscipline2.getMembers().contains(existing));
                });
    }

    @Test
    public void member_can_be_updated() throws Exception {
        Member member = memberRepository.save(createTestMember(contributionClassRepository));

        Long oldContributionClassId = member.getContributionClass().getId();

        ContributionClass contributionClass = contributionClassRepository.save(ContributionClass.builder()
                .name("Updated Contribution Class")
                .baseContribution(10.5)
                .additionalContribution(5)
                .build());

        // create two test disciplines
        Discipline discipline1 = disciplineRepository.save(Discipline.builder()
                .name("Test Discipline")
                .build());
        member.getDisciplines().add(discipline1);
        discipline1.getMembers().add(member);

        Discipline discipline2 = disciplineRepository.save(Discipline.builder()
                .name("Another Test Discipline")
                .build());

        // create two test events
        Event event1 = eventRepository.save(Event.builder()
                .name("Test Event")
                .date(LocalDate.parse("2018-01-02"))
                .build());
        member.getEvents().add(event1);
        event1.getMembers().add(member);

        Event event2 = eventRepository.save(Event.builder()
                .name("Another Test Event")
                .date(LocalDate.parse("2018-01-02"))
                .build());

        // create two test seminars
        Seminar seminar1 = seminarRepository.save(createTestSeminar());
        member.getSeminars().add(seminar1);
        seminar1.getMembers().add(member);

        Seminar seminar2 = seminarRepository.save(createTestSeminar());

        member = memberRepository.save(member);

        List<Long> disciplines = new ArrayList<Long>() {{
            add(discipline2.getId());
        }};

        List<Long> events = new ArrayList<Long>() {{
            add(event2.getId());
        }};

        List<Long> seminars = new ArrayList<Long>() {{
            add(seminar2.getId());
        }};

        // test member
        MemberUpdateDTO updateMember = MemberUpdateDTO.builder()
                .id(member.getId())
                .forename("Maxima")
                .surname("Musterfrau")
                .gender(Gender.APACHE_COMBAT_HELICOPTER)
                .street("Musterstraße 2")
                .postcode("26620")
                .phoneNumber("04929 5435438")
                .mobileNumber("1522 416845575")
                .email("newemail@internet.com")
                .dateOfBirth(LocalDate.parse("2018-01-02"))
                .hasBudoPass(true)
                .budoPassDate(LocalDate.parse("2018-01-02"))
                .enteredDate(LocalDate.parse("2018-01-02"))
                .hasLeft(true)
                .leftDate(LocalDate.parse("2018-01-03"))
                .isPassive(false)
                .contributionClass(contributionClass.getId())
                .notes("Test Notes")
                .disciplines(disciplines)
                .events(events)
                .seminars(seminars)
                .accountHolder("Max Mustermann")
                .build();

        String memberJson = TestHelper.toJson(updateMember);

        mockMvc.perform(put("/member")
                .contentType(APPLICATION_JSON_UTF8)
                .content(memberJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.forename").value(is(updateMember.getForename())))
                .andExpect(jsonPath("$.surname").value(is(updateMember.getSurname())))
                .andExpect(jsonPath("$.gender").value(is(updateMember.getGender().toString())))
                .andExpect(jsonPath("$.street").value(is(updateMember.getStreet())))
                .andExpect(jsonPath("$.postcode").value(is(updateMember.getPostcode())))
                .andExpect(jsonPath("$.phoneNumber").value(is(updateMember.getPhoneNumber())))
                .andExpect(jsonPath("$.mobileNumber").value(is(updateMember.getMobileNumber())))
                .andExpect(jsonPath("$.email").value(is(updateMember.getEmail())))
                .andExpect(jsonPath("$.dateOfBirth").value(is(updateMember.getDateOfBirth().toString())))
                .andExpect(jsonPath("$.hasBudoPass").value(is(updateMember.getHasBudoPass())))
                .andExpect(jsonPath("$.budoPassDate").value(is(updateMember.getBudoPassDate().toString())))
                .andExpect(jsonPath("$.enteredDate").value(is(updateMember.getEnteredDate().toString())))
                .andExpect(jsonPath("$.hasLeft").value(is(updateMember.getHasLeft())))
                .andExpect(jsonPath("$.leftDate").value(is(updateMember.getLeftDate().toString())))
                .andExpect(jsonPath("$.isPassive").value(is(updateMember.getIsPassive())))
                .andExpect(jsonPath("$.accountHolder").value(is(updateMember.getAccountHolder())))
                .andExpect(jsonPath("$.disciplines").value(hasSize(1)))
                .andExpect(jsonPath("$.disciplines[0].id").value(is(discipline2.getId().intValue())))
                .andExpect(jsonPath("$.events").value(hasSize(1)))
                .andExpect(jsonPath("$.events[0].id").value(is(event2.getId().intValue())))
                .andExpect(jsonPath("$.seminars").value(hasSize(1)))
                .andExpect(jsonPath("$.seminars[0].id").value(is(seminar2.getId().intValue())));

        final Discipline finalDiscipline1 = disciplineRepository.findById(discipline1.getId()).orElseThrow();
        final Discipline finalDiscipline2 = disciplineRepository.findById(discipline2.getId()).orElseThrow();

        final Event finalEvent1 = eventRepository.findById(event1.getId()).orElseThrow();
        final Event finalEvent2 = eventRepository.findById(event2.getId()).orElseThrow();

        final Seminar finalSeminar1 = seminarRepository.findById(seminar1.getId()).orElseThrow();
        final Seminar finalSeminar2 = seminarRepository.findById(seminar2.getId()).orElseThrow();

        final ContributionClass finalContributionClass = contributionClassRepository.findById(contributionClass.getId())
                .orElseThrow();

        final ContributionClass finalOldContributionClass = contributionClassRepository.findById(oldContributionClassId)
                .orElseThrow();

        memberRepository.findAll().forEach(
                existing -> {
                    // contribution class
                    assertEquals(0, finalOldContributionClass.getMembers().size());
                    assertEquals(1, finalContributionClass.getMembers().size());
                    assertNotNull(existing.getContributionClass());
                    assertEquals(finalContributionClass, existing.getContributionClass());

                    // disciplines
                    assertEquals(1, existing.getDisciplines().size());
                    assertEquals(0, finalDiscipline1.getMembers().size());
                    assertEquals(1, finalDiscipline2.getMembers().size());
                    assertFalse(existing.getDisciplines().contains(finalDiscipline1));
                    assertFalse(finalDiscipline1.getMembers().contains(existing));
                    assertTrue(existing.getDisciplines().contains(finalDiscipline2));
                    assertTrue(finalDiscipline2.getMembers().contains(existing));

                    // events
                    assertEquals(1, existing.getEvents().size());
                    assertEquals(0, finalEvent1.getMembers().size());
                    assertEquals(1, finalEvent2.getMembers().size());
                    assertFalse(existing.getEvents().contains(finalEvent1));
                    assertFalse(finalEvent1.getMembers().contains(existing));
                    assertTrue(existing.getEvents().contains(finalEvent2));
                    assertTrue(finalEvent2.getMembers().contains(existing));

                    // seminars
                    assertEquals(1, existing.getSeminars().size());
                    assertEquals(0, finalSeminar1.getMembers().size());
                    assertEquals(1, finalSeminar2.getMembers().size());
                    assertFalse(existing.getSeminars().contains(finalSeminar1));
                    assertFalse(finalSeminar1.getMembers().contains(existing));
                    assertTrue(existing.getSeminars().contains(finalSeminar2));
                    assertTrue(finalSeminar2.getMembers().contains(existing));
                });

        memberJson = memberJson.replace("\"id\":" + member.getId().toString(), "\"id\":-1");

        // try with invalid information
        mockMvc.perform(put("/member")
                .contentType(APPLICATION_JSON_UTF8)
                .content(memberJson))
                .andExpect(status().isConflict());
    }

    @Test
    public void member_can_be_deleted() throws Exception {
        Member member = memberRepository.save(createTestMember(contributionClassRepository));

        ContributionClass contributionClass = contributionClassRepository.save(ContributionClass.builder()
                .name("Test Contribution Class")
                .baseContribution(10.0)
                .additionalContribution(5.0)
                .build());

        Discipline discipline = disciplineRepository.save(Discipline.builder()
                .name("Test Discipline")
                .build());

        Event event = eventRepository.save(Event.builder()
                .name("Test Event")
                .date(LocalDate.parse("2018-01-02"))
                .build());

        Seminar seminar = seminarRepository.save(createTestSeminar());

        member.setContributionClass(contributionClass);
        member.getDisciplines().add(discipline);
        member.getEvents().add(event);
        member.getSeminars().add(seminar);

        contributionClass.getMembers().add(member);
        discipline.getMembers().add(member);
        event.getMembers().add(member);
        seminar.getMembers().add(member);

        member = memberRepository.save(member);

        assertTrue(contributionClass.getMembers().contains(member));
        assertTrue(discipline.getMembers().contains(member));
        assertTrue(event.getMembers().contains(member));
        assertTrue(seminar.getMembers().contains(member));

        mockMvc.perform(delete("/member/" + member.getId()))
                .andExpect(status().isNoContent());

        // check if the member still exists in the database
        assertFalse(memberRepository.findById(member.getId()).isPresent());

        contributionClass = contributionClassRepository.findById(contributionClass.getId())
                .orElseThrow(ContributionClassNotFoundException::new);

        discipline = disciplineRepository.findById(discipline.getId())
                .orElseThrow(DisciplineNotFoundException::new);

        event = eventRepository.findById(event.getId()).orElseThrow(EventNotFoundException::new);

        seminar = seminarRepository.findById(seminar.getId()).orElseThrow(SeminarNotFoundException::new);

        assertEquals(0, contributionClass.getMembers().size());
        assertEquals(0, discipline.getMembers().size());
        assertEquals(0, event.getMembers().size());
        assertEquals(0, seminar.getMembers().size());

        // check, if member is detached from contribution class, events and disciplines
        disciplineRepository.findAll().forEach(existing -> assertEquals(0, existing.getMembers().size()));
        eventRepository.findAll().forEach(existing -> assertEquals(0, existing.getMembers().size()));
        contributionClassRepository.findAll().forEach(existing ->
                assertEquals(0, existing.getMembers().size()));

        // test with not existing member
        mockMvc.perform(delete("/member/0"))
                .andExpect(status().isConflict());
    }

    @Test
    public void members_can_be_found_by_full_name() throws Exception {
        Member member = memberRepository.save(createTestMember(contributionClassRepository));

        mockMvc.perform(get("/member/byName/Max Mu"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$").value(hasSize(1)))
                .andExpect(jsonPath("$[0].forename").value(is(member.getForename())))
                .andExpect(jsonPath("$[0].surname").value(is(member.getSurname())))
                .andExpect(jsonPath("$[0].gender").value(is(member.getGender().toString())))
                .andExpect(jsonPath("$[0].street").value(is(member.getStreet())))
                .andExpect(jsonPath("$[0].postcode").value(is(member.getPostcode())))
                .andExpect(jsonPath("$[0].phoneNumber").value(is(member.getPhoneNumber())))
                .andExpect(jsonPath("$[0].mobileNumber").value(is(member.getMobileNumber())))
                .andExpect(jsonPath("$[0].email").value(is(member.getEmail())))
                .andExpect(jsonPath("$[0].dateOfBirth").value(is(member.getDateOfBirth().toString())))
                .andExpect(jsonPath("$[0].hasBudoPass").value(is(member.getHasBudoPass())))
                .andExpect(jsonPath("$[0].budoPassDate").value(is(member.getBudoPassDate().toString())))
                .andExpect(jsonPath("$[0].enteredDate").value(is(member.getEnteredDate().toString())))
                .andExpect(jsonPath("$[0].hasLeft").value(is(member.getHasLeft())))
                .andExpect(jsonPath("$[0].leftDate").value(is(member.getLeftDate().toString())))
                .andExpect(jsonPath("$[0].isPassive").value(is(member.getIsPassive())))
                .andExpect(jsonPath("$[0].accountHolder").value(is(member.getAccountHolder())))
                .andExpect(jsonPath("$[0].disciplines").value(is(member.getDisciplines())));

        // with not existing users
        mockMvc.perform(get("/member/byName/Not Existing"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$").value(hasSize(0)));
    }
}
