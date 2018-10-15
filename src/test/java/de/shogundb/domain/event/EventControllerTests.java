package de.shogundb.domain.event;

import com.google.gson.*;
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
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;

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
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    public void all_events_can_be_called() throws Exception {
        Event event = Event.builder()
                .name("Test Event")
                .date(new Date(1514764800000L))
                .build();

        event = this.eventRepository.save(event);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/event"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", is(event.getId().intValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", is(event.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].date", is(event.getDate().getTime())));
    }

    @Test
    public void event_can_be_added() throws Exception {
        Event event = Event.builder()
                .name("Test Event")
                .date(new Date(1514764800000L))
                .build();

        List<Long> members = new ArrayList<>();
        Member member = this.memberRepository.save(createTestMember());
        members.add(member.getId());

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json, typeOfT, context) -> new Date(json.getAsJsonPrimitive().getAsLong()))
                .registerTypeAdapter(Date.class, (JsonSerializer<Date>) (date, type, jsonSerializationContext) -> new JsonPrimitive(date.getTime()))
                .create();

        this.mockMvc.perform(MockMvcRequestBuilders.post("/event")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(EventRegisterDTO.builder()
                        .name(event.getName())
                        .date(event.getDate())
                        .members(members)
                        .build())))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", is(event.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.date", is(event.getDate().getTime())));

        final Member finalMember = this.memberRepository.findById(member.getId()).orElseThrow(MemberNotFoundException::new);

        assertEquals(1, this.eventRepository.count());

        this.eventRepository.findAll().forEach(
                existing -> {
                    assertEquals(1, existing.getMembers().size());
                    assertTrue(existing.getMembers().contains(finalMember));
                    assertEquals(1, finalMember.getEvents().size());
                    assertTrue(finalMember.getEvents().contains(existing));
                });

        this.mockMvc.perform(MockMvcRequestBuilders.post("/event")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(EventRegisterDTO.builder()
                        .name(event.getName())
                        .date(event.getDate())
                        .members(Collections.singletonList(-1L))
                        .build())))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    public void event_can_be_updated() throws Exception {
        Event event = this.eventRepository.save(Event.builder()
                .name("Test Event")
                .date(new Date(1514764800000L))
                .build());

        event.setName("Updated Event");
        event.setDate(new Date(810086400000L));

        List<Long> members = new ArrayList<>();
        Member member = this.memberRepository.save(createTestMember());
        members.add(member.getId());

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json, typeOfT, context) -> new Date(json.getAsJsonPrimitive().getAsLong()))
                .registerTypeAdapter(Date.class, (JsonSerializer<Date>) (date, type, jsonSerializationContext) -> new JsonPrimitive(date.getTime()))
                .create();

        this.mockMvc.perform(MockMvcRequestBuilders.put("/event")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(EventUpdateDTO.builder()
                        .id(event.getId())
                        .name(event.getName())
                        .date(event.getDate())
                        .members(members)
                        .build())))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", is(event.getId().intValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", is(event.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.date", is(event.getDate().getTime())));

        event = this.eventRepository.findById(event.getId()).orElseThrow(EventNotFoundException::new);
        member = this.memberRepository.findById(member.getId()).orElseThrow(MemberNotFoundException::new);

        assertEquals(member.getId(), event.getMembers().get(0).getId());
        assertEquals("Updated Event", event.getName());
        assertEquals(new Date(810086400000L), event.getDate());
        assertEquals(1, event.getMembers().size());
        assertTrue(event.getMembers().contains(member));
        assertEquals(1, member.getEvents().size());
        assertTrue(member.getEvents().contains(event));
    }

    @Test
    public void event_can_be_deleted() throws Exception {
        Event event = this.eventRepository.save(Event.builder()
                .name("Test Event")
                .date(new Date(1514764800000L))
                .build());

        Member member1 = this.memberRepository.save(this.createTestMember());
        Member member2 = this.memberRepository.save(this.createTestMember());

        member1.getEvents().add(event);
        member2.getEvents().add(event);

        event.getMembers().add(member1);
        event.getMembers().add(member2);

        event = this.eventRepository.save(event);

        this.mockMvc.perform(MockMvcRequestBuilders.delete("/event/" + event.getId()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        assertFalse(this.eventRepository.findById(event.getId()).isPresent());

        member1 = this.memberRepository.findById(member1.getId()).orElseThrow(MemberNotFoundException::new);
        member2 = this.memberRepository.findById(member2.getId()).orElseThrow(MemberNotFoundException::new);

        assertEquals(0, member1.getEvents().size());
        assertEquals(0, member2.getEvents().size());

        this.mockMvc.perform(MockMvcRequestBuilders.delete("/event/-1"))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    public void event_can_be_called_by_id() throws Exception {
        Event event = this.eventRepository.save(Event.builder()
                .name("Test Event")
                .date(new Date(1514764800000L))
                .build());

        this.mockMvc.perform(MockMvcRequestBuilders.get("/event/" + event.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", is(event.getId().intValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", is(event.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.date", is(event.getDate().getTime())));

        this.mockMvc.perform(MockMvcRequestBuilders.get("/event/-1"))
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
