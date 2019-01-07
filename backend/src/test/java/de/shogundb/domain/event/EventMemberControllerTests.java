package de.shogundb.domain.event;

import de.shogundb.domain.contributionClass.ContributionClassRepository;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;

import static de.shogundb.TestHelper.createTestMember;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void all_members_of_an_event_can_be_called() throws Exception {
        Member member = memberRepository.save(createTestMember(contributionClassRepository));

        Event event = eventRepository.save(Event.builder()
                .name("Test Event")
                .date(LocalDate.parse("2018-01-02"))
                .build());

        member.getEvents().add(event);
        event.getMembers().add(member);
        event = eventRepository.save(event);

        mockMvc.perform(get("/event/member/" + event.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(is(member.getId().intValue())));

        mockMvc.perform(get("/event/member/-1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void member_can_be_added_to_event() throws Exception {
        Member member = memberRepository.save(createTestMember(contributionClassRepository));

        Event event = eventRepository.save(Event.builder()
                .name("Test Event")
                .date(LocalDate.parse("2018-01-02"))
                .build());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/event/member/" + event.getId() + "/" + member.getId()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").value(hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(is(member.getId().intValue())))
                .andExpect(jsonPath("$[0].events").value(hasSize(1)))
                .andExpect(jsonPath("$[0].events[0].id").value(is(event.getId().intValue())));

        member = memberRepository.findById(member.getId()).orElseThrow(MemberNotFoundException::new);
        event = eventRepository.findById(event.getId()).orElseThrow(EventNotFoundException::new);

        assertEquals(1, event.getMembers().size());
        assertEquals(1, member.getEvents().size());

        assertTrue(event.getMembers().contains(member));
        assertTrue(member.getEvents().contains(event));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/event/member/" + event.getId() + "/" + member.getId()))
                .andExpect(status().isConflict());
    }

    @Test
    public void member_can_be_removed_from_event() throws Exception {
        Member member = memberRepository.save(createTestMember(contributionClassRepository));

        Event event = eventRepository.save(Event.builder()
                .name("Test Event")
                .date(LocalDate.parse("2018-01-02"))
                .build());

        member.getEvents().add(event);
        event.getMembers().add(member);

        eventRepository.save(event);

        mockMvc.perform(MockMvcRequestBuilders
                .delete("/event/member/" + event.getId() + "/" + member.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(hasSize(0)));

        member = memberRepository.findById(member.getId()).orElseThrow(MemberNotFoundException::new);
        event = eventRepository.findById(event.getId()).orElseThrow(EventNotFoundException::new);

        assertEquals(0, event.getMembers().size());
        assertEquals(0, member.getEvents().size());

        mockMvc.perform(MockMvcRequestBuilders
                .delete("/event/member/" + event.getId() + "/" + member.getId()))
                .andExpect(status().isConflict());
    }
}
