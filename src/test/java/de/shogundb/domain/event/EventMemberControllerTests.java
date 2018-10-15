package de.shogundb.domain.event;

import de.shogundb.domain.contributionClass.ContributionClass;
import de.shogundb.domain.contributionClass.ContributionClassRepository;
import de.shogundb.domain.member.Gender;
import de.shogundb.domain.member.Member;
import de.shogundb.domain.member.MemberNotFoundException;
import de.shogundb.domain.member.MemberRepository;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles(profiles = "test")
@AutoConfigureTestDatabase
@Transactional
public class EventMemberControllerTests {
    @Autowired
    private EventRepository eventRepository;

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
    public void all_members_of_an_event_can_be_called() throws Exception {
        Member member = this.memberRepository.save(createTestMember());

        Event event = this.eventRepository.save(Event.builder()
                .name("Test Event")
                .date(new Date(1514764800000L))
                .build());

        member.getEvents().add(event);
        event.getMembers().add(member);
        event = this.eventRepository.save(event);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/event/member/" + event.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", is(member.getId().intValue())));

        this.mockMvc.perform(MockMvcRequestBuilders.get("/event/member/-1"))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    public void member_can_be_added_to_event() throws Exception {
        Member member = this.memberRepository.save(createTestMember());

        Event event = this.eventRepository.save(Event.builder()
                .name("Test Event")
                .date(new Date(1514764800000L))
                .build());

        this.mockMvc.perform(MockMvcRequestBuilders
                .post("/event/member/" + event.getId() + "/" + member.getId()))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", is(member.getId().intValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].events", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].events[0].id", is(event.getId().intValue())));

        member = this.memberRepository.findById(member.getId()).orElseThrow(MemberNotFoundException::new);
        event = this.eventRepository.findById(event.getId()).orElseThrow(EventNotFoundException::new);

        assertEquals(1, event.getMembers().size());
        assertEquals(1, member.getEvents().size());

        assertTrue(event.getMembers().contains(member));
        assertTrue(member.getEvents().contains(event));

        this.mockMvc.perform(MockMvcRequestBuilders
                .post("/event/member/" + event.getId() + "/" + member.getId()))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    public void member_can_be_removed_from_event() throws Exception {
        Member member = this.memberRepository.save(createTestMember());

        Event event = this.eventRepository.save(Event.builder()
                .name("Test Event")
                .date(new Date(1514764800000L))
                .build());

        member.getEvents().add(event);
        event.getMembers().add(member);

        this.eventRepository.save(event);

        this.mockMvc.perform(MockMvcRequestBuilders
                .delete("/event/member/" + event.getId() + "/" + member.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(0)));

        member = this.memberRepository.findById(member.getId()).orElseThrow(MemberNotFoundException::new);
        event = this.eventRepository.findById(event.getId()).orElseThrow(EventNotFoundException::new);

        assertEquals(0, event.getMembers().size());
        assertEquals(0, member.getEvents().size());

        this.mockMvc.perform(MockMvcRequestBuilders
                .delete("/event/member/" + event.getId() + "/" + member.getId()))
                .andExpect(MockMvcResultMatchers.status().isConflict());
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
