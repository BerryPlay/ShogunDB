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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static de.shogundb.TestHelper.createTestMember;
import static de.shogundb.TestHelper.toJson;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles(profiles = "test")
@AutoConfigureTestDatabase
@Transactional
public class EventControllerTests {

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
    public void all_events_can_be_called() throws Exception {
        Event event = Event.builder()
                .name("Test Event")
                .date(LocalDate.parse("2018-01-02"))
                .build();

        event = eventRepository.save(event);

        mockMvc.perform(get("/event"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(is(event.getId().intValue())))
                .andExpect(jsonPath("$[0].name").value(is(event.getName())))
                .andExpect(jsonPath("$[0].date").value(is(event.getDate().toString())));
    }

    @Test
    public void event_can_be_added() throws Exception {
        Event event = Event.builder()
                .name("Test Event")
                .date(LocalDate.parse("2018-01-02"))
                .build();

        List<Long> members = new ArrayList<>();
        Member member = memberRepository.save(createTestMember(contributionClassRepository));
        members.add(member.getId());

        mockMvc.perform(post("/event")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(EventRegisterDTO.builder()
                        .name(event.getName())
                        .date(event.getDate())
                        .members(members)
                        .build())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(notNullValue()))
                .andExpect(jsonPath("$.name").value(is(event.getName())))
                .andExpect(jsonPath("$.date").value(is(event.getDate().toString())));

        final Member finalMember = memberRepository.findById(member.getId()).orElseThrow(MemberNotFoundException::new);

        assertEquals(1, eventRepository.count());

        eventRepository.findAll().forEach(
                existing -> {
                    assertEquals(1, existing.getMembers().size());
                    assertTrue(existing.getMembers().contains(finalMember));
                    assertEquals(1, finalMember.getEvents().size());
                    assertTrue(finalMember.getEvents().contains(existing));
                });

        mockMvc.perform(post("/event")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(EventRegisterDTO.builder()
                        .name(event.getName())
                        .date(event.getDate())
                        .members(Collections.singletonList(-1L))
                        .build())))
                .andExpect(status().isNotFound());
    }

    @Test
    public void event_can_be_updated() throws Exception {
        Event event = eventRepository.save(Event.builder()
                .name("Test Event")
                .date(LocalDate.parse("2018-01-02"))
                .build());

        event.setName("Updated Event");
        event.setDate(LocalDate.parse("2018-01-02"));

        List<Long> members = new ArrayList<>();
        Member member = memberRepository.save(createTestMember(contributionClassRepository));
        members.add(member.getId());

        mockMvc.perform(put("/event")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(EventUpdateDTO.builder()
                        .id(event.getId())
                        .name(event.getName())
                        .date(event.getDate())
                        .members(members)
                        .build())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(is(event.getId().intValue())))
                .andExpect(jsonPath("$.name").value(is(event.getName())))
                .andExpect(jsonPath("$.date").value(is(event.getDate().toString())));

        event = eventRepository.findById(event.getId()).orElseThrow(EventNotFoundException::new);
        member = memberRepository.findById(member.getId()).orElseThrow(MemberNotFoundException::new);

        assertEquals(member.getId(), event.getMembers().get(0).getId());
        assertEquals("Updated Event", event.getName());
        assertEquals(LocalDate.parse("2018-01-02"), event.getDate());
        assertEquals(1, event.getMembers().size());
        assertTrue(event.getMembers().contains(member));
        assertEquals(1, member.getEvents().size());
        assertTrue(member.getEvents().contains(event));
    }

    @Test
    public void event_can_be_deleted() throws Exception {
        Event event = eventRepository.save(Event.builder()
                .name("Test Event")
                .date(LocalDate.parse("2018-01-02"))
                .build());

        Member member1 = memberRepository.save(createTestMember(contributionClassRepository));
        Member member2 = memberRepository.save(createTestMember(contributionClassRepository));

        member1.getEvents().add(event);
        member2.getEvents().add(event);

        event.getMembers().add(member1);
        event.getMembers().add(member2);

        event = eventRepository.save(event);

        mockMvc.perform(delete("/event/" + event.getId()))
                .andExpect(status().isNoContent());

        assertFalse(eventRepository.findById(event.getId()).isPresent());

        member1 = memberRepository.findById(member1.getId()).orElseThrow(MemberNotFoundException::new);
        member2 = memberRepository.findById(member2.getId()).orElseThrow(MemberNotFoundException::new);

        assertEquals(0, member1.getEvents().size());
        assertEquals(0, member2.getEvents().size());

        mockMvc.perform(delete("/event/-1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void event_can_be_called_by_id() throws Exception {
        Event event = eventRepository.save(Event.builder()
                .name("Test Event")
                .date(LocalDate.parse("2018-01-02"))
                .build());

        mockMvc.perform(get("/event/" + event.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(is(event.getId().intValue())))
                .andExpect(jsonPath("$.name").value(is(event.getName())))
                .andExpect(jsonPath("$.date").value(is(event.getDate().toString())));

        mockMvc.perform(get("/event/-1"))
                .andExpect(status().isNotFound());
    }
}
