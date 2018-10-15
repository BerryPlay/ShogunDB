package de.shogundb.domain.championship;

import com.google.gson.*;
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
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles(profiles = "test")
@AutoConfigureTestDatabase
@Transactional
public class ChampionshipControllerTests {
    @Autowired
    private ChampionshipRepository championshipRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ChampionshipMemberRepository championshipMemberRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    public void all_championships_can_be_called() throws Exception {
        Championship championship = this.championshipRepository.save(Championship.builder()
                .name("Test Championship")
                .date(new Date(1514764800000L))
                .build());

        this.mockMvc.perform(MockMvcRequestBuilders.get("/championship"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", is(championship.getId().intValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", is(championship.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].date", is(championship.getDate().getTime())));
    }

    @Test
    public void championship_can_be_added() throws Exception {
        Member member1 = this.memberRepository.save(this.createTestMember());
        Member member2 = this.memberRepository.save(this.createTestMember());

        List<ChampionshipMemberRegisterDTO> members = new ArrayList<>();
        members.add(ChampionshipMemberRegisterDTO.builder()
                .memberId(member1.getId())
                .extra("Test Extra")
                .build());
        members.add(ChampionshipMemberRegisterDTO.builder()
                .memberId(member2.getId())
                .rank(1)
                .build());

        ChampionshipRegisterDTO championshipRegisterDTO = ChampionshipRegisterDTO.builder()
                .name("Test Championship")
                .date(new Date(1514764800000L))
                .members(members)
                .build();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json, typeOfT, context) -> new Date(json.getAsJsonPrimitive().getAsLong()))
                .registerTypeAdapter(Date.class, (JsonSerializer<Date>) (date, type, jsonSerializationContext) -> new JsonPrimitive(date.getTime()))
                .create();

        this.mockMvc.perform(MockMvcRequestBuilders.post("/championship")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(gson.toJson(championshipRegisterDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", is(championshipRegisterDTO.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.date", is(championshipRegisterDTO.getDate().getTime())));

        assertEquals(1L, this.championshipRepository.count());

        this.championshipRepository.findAll().forEach(
                existing -> assertEquals(2, existing.getMembers().size()));

        assertEquals(2L, this.championshipMemberRepository.count());

        List<Member> memberList = new ArrayList<>();
        memberList.add(member1);
        memberList.add(member2);

        this.championshipMemberRepository.findAll().forEach(
                existing -> {
                    assertTrue(memberList.contains(existing.getMember()));
                    try {
                        this.championshipRepository.findById(existing.getChampionship().getId()).orElseThrow(ChampionshipNotFoundException::new);
                    } catch (ChampionshipNotFoundException e) {
                        fail();
                    }
                });
    }

    @Test
    public void championship_can_be_updated() throws Exception {
        Member member1 = this.memberRepository.save(this.createTestMember());
        Member member2 = this.memberRepository.save(this.createTestMember());

        Championship championship = this.championshipRepository.save(Championship.builder()
                .name("Test Championship")
                .date(new Date(1514764800000L))
                .build());

        ChampionshipMember championshipMember = ChampionshipMember.builder()
                .member(member1)
                .championship(championship)
                .rank(1)
                .extra("Test Extra")
                .build();

        member1.getChampionships().add(championshipMember);
        championship.getMembers().add(championshipMember);

        championship = this.championshipRepository.save(championship);

        ChampionshipMemberRegisterDTO championshipMemberRegisterDTO = ChampionshipMemberRegisterDTO.builder()
                .memberId(member2.getId())
                .extra("Extra")
                .rank(1)
                .build();

        List<ChampionshipMemberRegisterDTO> members = new ArrayList<>();
        members.add(championshipMemberRegisterDTO);

        ChampionshipUpdateDTO championshipUpdateDTO = ChampionshipUpdateDTO.builder()
                .id(championship.getId())
                .name("Updated Championship")
                .date(new Date(1514764800800L))
                .members(members)
                .build();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json, typeOfT, context) -> new Date(json.getAsJsonPrimitive().getAsLong()))
                .registerTypeAdapter(Date.class, (JsonSerializer<Date>) (date, type, jsonSerializationContext) -> new JsonPrimitive(date.getTime()))
                .create();

        this.mockMvc.perform(MockMvcRequestBuilders.put("/championship")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(gson.toJson(championshipUpdateDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", is(championshipUpdateDTO.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.date", is(championshipUpdateDTO.getDate().getTime())));

        member1 = this.memberRepository.findById(member1.getId()).orElseThrow(MemberNotFoundException::new);
        member2 = this.memberRepository.findById(member2.getId()).orElseThrow(MemberNotFoundException::new);

        assertTrue(member1.getChampionships().isEmpty());
        assertFalse(member2.getChampionships().isEmpty());
        assertEquals(member2, member2.getChampionships().get(0).getMember());

        championship = this.championshipRepository.findById(championship.getId())
                .orElseThrow(ChampionshipNotFoundException::new);

        assertEquals(1, championship.getMembers().size());
        assertEquals(member2, championship.getMembers().get(0).getMember());
        assertEquals(championship, member2.getChampionships().get(0).getChampionship());
        assertEquals(championshipMemberRegisterDTO.getExtra(), member2.getChampionships().get(0).getExtra());
        assertEquals(championshipMemberRegisterDTO.getRank(), member2.getChampionships().get(0).getRank());
    }

    @Test
    public void championship_can_be_deleted() throws Exception {
        Member member = this.memberRepository.save(this.createTestMember());

        Championship championship = this.championshipRepository.save(Championship.builder()
                .name("Test Championship")
                .date(new Date(1514764800000L))
                .build());

        ChampionshipMember championshipMember = ChampionshipMember.builder()
                .member(member)
                .championship(championship)
                .rank(1)
                .extra("Test Extra")
                .build();

        member.getChampionships().add(championshipMember);
        championship.getMembers().add(championshipMember);

        championship = this.championshipRepository.save(championship);

        this.mockMvc.perform(MockMvcRequestBuilders.delete("/championship/" + championship.getId()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        member = this.memberRepository.findById(member.getId()).orElseThrow(MemberNotFoundException::new);

        assertTrue(member.getChampionships().isEmpty());
        assertFalse(this.championshipRepository.findById(championship.getId()).isPresent());
        assertEquals(0, this.championshipMemberRepository.count());

        this.mockMvc.perform(MockMvcRequestBuilders.delete("/championship/-1"))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    public void championship_can_be_called_by_id() throws Exception {
        Championship championship = this.championshipRepository.save(Championship.builder()
                .name("Test Championship")
                .date(new Date(1514764800000L))
                .build());

        this.mockMvc.perform(MockMvcRequestBuilders.get("/championship/" + championship.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", is(championship.getId().intValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", is(championship.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.date", is(championship.getDate().getTime())));

        this.mockMvc.perform(MockMvcRequestBuilders.get("/championship/-1"))
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
                .contributionClass(null)
                .accountHolder("Max Mustermann")
                .build();
    }
}
