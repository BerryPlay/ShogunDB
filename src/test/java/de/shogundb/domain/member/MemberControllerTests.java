package de.shogundb.domain.member;

import com.google.gson.*;
import de.shogundb.domain.contributionClass.ContributionClass;
import de.shogundb.domain.contributionClass.ContributionClassNotFoundException;
import de.shogundb.domain.contributionClass.ContributionClassRepository;
import de.shogundb.domain.discipline.Discipline;
import de.shogundb.domain.discipline.DisciplineNotFoundException;
import de.shogundb.domain.discipline.DisciplineRepository;
import de.shogundb.domain.event.Event;
import de.shogundb.domain.event.EventNotFoundException;
import de.shogundb.domain.event.EventRepository;
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
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

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
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    public void all_members_can_be_called() throws Exception {
        // insert a test member
        Member member = this.memberRepository.save(this.createTestMember());

        System.out.println("Member as JSON: " + member.getDateOfBirth());

        this.mockMvc.perform(MockMvcRequestBuilders.get("/member"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].forename", is(member.getForename())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].surname", is(member.getSurname())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].gender", is(member.getGender().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].street", is(member.getStreet())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].postcode", is(member.getPostcode())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].phoneNumber", is(member.getPhoneNumber())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].mobileNumber", is(member.getMobileNumber())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].email", is(member.getEmail())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].dateOfBirth", is(member.getDateOfBirth().getTime())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].hasBudoPass", is(member.getHasBudoPass())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].budoPassDate", is(member.getBudoPassDate().getTime())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].enteredDate", is(member.getEnteredDate().getTime())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].hasLeft", is(member.getHasLeft())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].leftDate", is(member.getLeftDate())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].isPassive", is(member.getIsPassive())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].accountHolder", is(member.getAccountHolder())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].disciplines", is(member.getDisciplines())));
    }

    @Test
    public void member_can_be_added() throws Exception {
        ContributionClass contributionClass = this.contributionClassRepository.save(
                ContributionClass.builder()
                        .name("Test")
                        .baseContribution(27.7)
                        .additionalContribution(5)
                        .build());

        Discipline discipline1 = this.disciplineRepository.save(Discipline.builder()
                .name("Test Discipline")
                .build());

        Discipline discipline2 = this.disciplineRepository.save(Discipline.builder()
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
                .dateOfBirth(new Date(810086400000L))
                .hasBudoPass(true)
                .budoPassDate(new Date(1514764800000L))
                .enteredDate(new Date(1514764800000L))
                .hasLeft(false)
                .leftDate(null)
                .isPassive(false)
                .contributionClass(contributionClass.getId())
                .accountHolder("Max Mustermann")
                .disciplines(disciplines)
                .build();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json, typeOfT, context) -> new Date(json.getAsJsonPrimitive().getAsLong()))
                .registerTypeAdapter(Date.class, (JsonSerializer<Date>) (date, type, jsonSerializationContext) -> new JsonPrimitive(date.getTime()))
                .create();

        String memberJson = gson.toJson(memberRegisterDTO);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/member")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(memberJson))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("$.forename", is(memberRegisterDTO.getForename())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.surname", is(memberRegisterDTO.getSurname())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.gender", is(memberRegisterDTO.getGender().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.street", is(memberRegisterDTO.getStreet())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.postcode", is(memberRegisterDTO.getPostcode())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.phoneNumber", is(memberRegisterDTO.getPhoneNumber())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.mobileNumber", is(memberRegisterDTO.getMobileNumber())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", is(memberRegisterDTO.getEmail())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dateOfBirth", is(memberRegisterDTO.getDateOfBirth().getTime())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hasBudoPass", is(memberRegisterDTO.getHasBudoPass())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.budoPassDate", is(memberRegisterDTO.getBudoPassDate().getTime())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.enteredDate", is(memberRegisterDTO.getEnteredDate().getTime())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hasLeft", is(memberRegisterDTO.getHasLeft())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.leftDate", is(memberRegisterDTO.getLeftDate())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isPassive", is(memberRegisterDTO.getIsPassive())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.accountHolder", is(memberRegisterDTO.getAccountHolder())));

        final ContributionClass finalContributionClass = this.contributionClassRepository.findById(contributionClass.getId())
                .orElseThrow(ContributionClassNotFoundException::new);

        final Discipline finalDiscipline1 = this.disciplineRepository.findById(discipline1.getId())
                .orElseThrow(DisciplineNotFoundException::new);

        final Discipline finalDiscipline2 = this.disciplineRepository.findById(discipline2.getId())
                .orElseThrow(DisciplineNotFoundException::new);

        assertEquals(1, this.memberRepository.count());

        this.memberRepository.findAll().forEach(
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
        Member member = this.memberRepository.save(this.createTestMember());

        Long oldContributionClassId = member.getContributionClass().getId();

        ContributionClass contributionClass = this.contributionClassRepository.save(ContributionClass.builder()
                .name("Updated Contribution Class")
                .baseContribution(10.5)
                .additionalContribution(5)
                .build());

        Discipline discipline1 = this.disciplineRepository.save(Discipline.builder()
                .name("Test Discipline")
                .build());
        member.getDisciplines().add(discipline1);
        discipline1.getMembers().add(member);

        Discipline discipline2 = this.disciplineRepository.save(Discipline.builder()
                .name("Another Test Discipline")
                .build());

        Event event1 = this.eventRepository.save(Event.builder()
                .name("Test Event")
                .date(new Date(1523318400000L))
                .build());
        member.getEvents().add(event1);
        event1.getMembers().add(member);

        Event event2 = this.eventRepository.save(Event.builder()
                .name("Another Test Event")
                .date(new Date(1523318400000L))
                .build());

        member = this.memberRepository.save(member);

        List<Long> disciplines = new ArrayList<>();
        List<Long> events = new ArrayList<>();

        disciplines.add(discipline2.getId());
        events.add(event2.getId());

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
                .dateOfBirth(new Date(810086400000L))
                .hasBudoPass(true)
                .budoPassDate(new Date(1523318400000L))
                .enteredDate(new Date(1514764800000L))
                .hasLeft(false)
                .leftDate(null)
                .isPassive(false)
                .contributionClass(contributionClass.getId())
                .notes("Test Notes")
                .disciplines(disciplines)
                .events(events)
                .accountHolder("Max Mustermann")
                .build();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json, typeOfT, context) -> new Date(json.getAsJsonPrimitive().getAsLong()))
                .registerTypeAdapter(Date.class, (JsonSerializer<Date>) (date, type, jsonSerializationContext) -> new JsonPrimitive(date.getTime()))
                .create();

        String memberJson = gson.toJson(updateMember);

        this.mockMvc.perform(MockMvcRequestBuilders.put("/member")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(memberJson))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("$.forename", is(updateMember.getForename())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.surname", is(updateMember.getSurname())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.gender", is(updateMember.getGender().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.street", is(updateMember.getStreet())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.postcode", is(updateMember.getPostcode())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.phoneNumber", is(updateMember.getPhoneNumber())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.mobileNumber", is(updateMember.getMobileNumber())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", is(updateMember.getEmail())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dateOfBirth", is(updateMember.getDateOfBirth().getTime())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hasBudoPass", is(updateMember.getHasBudoPass())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.budoPassDate", is(updateMember.getBudoPassDate().getTime())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.enteredDate", is(updateMember.getEnteredDate().getTime())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hasLeft", is(updateMember.getHasLeft())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.leftDate", is(updateMember.getLeftDate())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isPassive", is(updateMember.getIsPassive())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.accountHolder", is(updateMember.getAccountHolder())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.disciplines", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.disciplines[0].id", is(discipline2.getId().intValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.events", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.events[0].id", is(event2.getId().intValue())));

        final Discipline finalDiscipline1 = this.disciplineRepository.findById(discipline1.getId()).orElseThrow(DisciplineNotFoundException::new);
        final Discipline finalDiscipline2 = this.disciplineRepository.findById(discipline2.getId()).orElseThrow(DisciplineNotFoundException::new);

        final Event finalEvent1 = this.eventRepository.findById(event1.getId()).orElseThrow(EventNotFoundException::new);
        final Event finalEvent2 = this.eventRepository.findById(event2.getId()).orElseThrow(EventNotFoundException::new);

        final ContributionClass finalContributionClass = this.contributionClassRepository
                .findById(contributionClass.getId())
                .orElseThrow(ContributionClassNotFoundException::new);

        final ContributionClass finalOldContributionClass = this.contributionClassRepository
                .findById(oldContributionClassId)
                .orElseThrow(ContributionClassNotFoundException::new);

        this.memberRepository.findAll().forEach(
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
                });

        memberJson = memberJson.replace("\"id\":" + member.getId().toString(), "\"id\":-1");

        // try with invalid information
        mockMvc.perform(MockMvcRequestBuilders.put("/member")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(memberJson))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    public void member_can_be_deleted() throws Exception {
        Member member = this.memberRepository.save(this.createTestMember());

        ContributionClass contributionClass = this.contributionClassRepository.save(ContributionClass.builder()
                .name("Test Contribution Class")
                .baseContribution(10.0)
                .additionalContribution(5.0)
                .build());

        Discipline discipline = this.disciplineRepository.save(Discipline.builder()
                .name("Test Discipline")
                .build());

        Event event = this.eventRepository.save(Event.builder()
                .name("Test Event")
                .date(new Date(1514764800000L))
                .build());

        member.setContributionClass(contributionClass);
        member.getDisciplines().add(discipline);
        member.getEvents().add(event);

        contributionClass.getMembers().add(member);
        discipline.getMembers().add(member);
        event.getMembers().add(member);

        member = this.memberRepository.save(member);

        assertTrue(contributionClass.getMembers().contains(member));
        assertTrue(discipline.getMembers().contains(member));
        assertTrue(event.getMembers().contains(member));

        this.mockMvc.perform(MockMvcRequestBuilders.delete("/member/" + member.getId()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        // check if the member still exists in the database
        assertFalse(this.memberRepository.findById(member.getId()).isPresent());

        contributionClass = this.contributionClassRepository.findById(contributionClass.getId())
                .orElseThrow(ContributionClassNotFoundException::new);

        discipline = this.disciplineRepository.findById(discipline.getId())
                .orElseThrow(DisciplineNotFoundException::new);

        event = this.eventRepository.findById(event.getId()).orElseThrow(EventNotFoundException::new);

        assertEquals(0,contributionClass.getMembers().size());
        assertEquals(0,discipline.getMembers().size());
        assertEquals(0,event.getMembers().size());

        // check, if member is detached from contribution class, events and disciplines
        this.disciplineRepository.findAll().forEach(existing -> assertEquals(0, existing.getMembers().size()));
        this.eventRepository.findAll().forEach(existing -> assertEquals(0, existing.getMembers().size()));
        this.contributionClassRepository.findAll().forEach(existing -> assertEquals(0, existing.getMembers().size()));

        // test with not existing member
        mockMvc.perform(MockMvcRequestBuilders.delete("/member/0"))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    public void members_can_be_found_by_full_name() throws Exception {
        Member member = this.memberRepository.save(this.createTestMember());

        mockMvc.perform(MockMvcRequestBuilders.get("/member/byName/Max Mu"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].forename", is(member.getForename())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].surname", is(member.getSurname())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].gender", is(member.getGender().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].street", is(member.getStreet())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].postcode", is(member.getPostcode())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].phoneNumber", is(member.getPhoneNumber())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].mobileNumber", is(member.getMobileNumber())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].email", is(member.getEmail())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].dateOfBirth", is(member.getDateOfBirth().getTime())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].hasBudoPass", is(member.getHasBudoPass())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].budoPassDate", is(member.getBudoPassDate().getTime())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].enteredDate", is(member.getEnteredDate().getTime())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].hasLeft", is(member.getHasLeft())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].leftDate", is(member.getLeftDate())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].isPassive", is(member.getIsPassive())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].accountHolder", is(member.getAccountHolder())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].disciplines", is(member.getDisciplines())));

        // with not existing users
        mockMvc.perform(MockMvcRequestBuilders.get("/member/byName/Not Existing"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(0)));
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
