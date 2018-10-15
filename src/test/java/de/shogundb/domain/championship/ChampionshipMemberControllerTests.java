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

import java.util.Date;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles(profiles = "test")
@AutoConfigureTestDatabase
@Transactional

public class ChampionshipMemberControllerTests {
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
    public void all_members_of_championships_can_be_called() throws Exception {
        Member member = this.memberRepository.save(this.createTestMember());

        Championship championship = this.championshipRepository.save(Championship.builder()
                .name("Test Championship")
                .date(new Date(1514764800000L))
                .build());

        ChampionshipMember championshipMember = this.championshipMemberRepository.save(ChampionshipMember.builder()
                .championship(championship)
                .member(member)
                .extra("Gew. Klasse bis 50kg")
                .rank(2)
                .build());

        member.getChampionships().add(championshipMember);
        championship.getMembers().add(championshipMember);

        championshipMember = this.championshipMemberRepository.save(championshipMember);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/championship/member/" + championship.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", is(championshipMember.getId().intValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].rank", is(championshipMember.getRank())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].extra", is(championshipMember.getExtra())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].member.id", is(member.getId().intValue())));
    }

    @Test
    public void member_can_be_added_to_championship() throws Exception {
        Member member = this.memberRepository.save(this.createTestMember());
        Championship championship = this.championshipRepository.save(Championship.builder()
                .name("Test Championship")
                .date(new Date(1514764800000L))
                .build());

        ChampionshipMemberRegisterDTO championshipMemberRegisterDTO = ChampionshipMemberRegisterDTO.builder()
                .memberId(member.getId())
                .rank(2)
                .extra("Test Extra")
                .build();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json, typeOfT, context) -> new Date(json.getAsJsonPrimitive().getAsLong()))
                .registerTypeAdapter(Date.class, (JsonSerializer<Date>) (date, type, jsonSerializationContext) -> new JsonPrimitive(date.getTime()))
                .create();

        this.mockMvc.perform(MockMvcRequestBuilders.post("/championship/member/" + championship.getId())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(gson.toJson(championshipMemberRegisterDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.championship.id", is(championship.getId().intValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.rank", is(championshipMemberRegisterDTO.getRank())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.extra", is(championshipMemberRegisterDTO.getExtra())));

        member = this.memberRepository.findById(member.getId()).orElseThrow(MemberNotFoundException::new);
        championship = this.championshipRepository.findById(championship.getId()).orElseThrow(ChampionshipNotFoundException::new);

        assertEquals(member, championship.getMembers().get(0).getMember());
        assertEquals(championship, member.getChampionships().get(0).getChampionship());

        assertEquals(championshipMemberRegisterDTO.getRank(), member.getChampionships().get(0).getRank());
        assertEquals(championshipMemberRegisterDTO.getExtra(), championship.getMembers().get(0).getExtra());

        this.mockMvc.perform(MockMvcRequestBuilders.post("/championship/member/" + championship.getId())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(gson.toJson(championshipMemberRegisterDTO)))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }


    @Test
    public void championship_member_can_be_updated() throws Exception {
        Member member = this.memberRepository.save(this.createTestMember());
        Championship championship = this.championshipRepository.save(Championship.builder()
                .name("Test Championship")
                .date(new Date(1514764800000L))
                .build());

        ChampionshipMember championshipMember = this.championshipMemberRepository.save(ChampionshipMember.builder()
                .member(member)
                .championship(championship)
                .rank(1)
                .extra("Test Extra")
                .build());

        ChampionshipMemberDTO championshipMemberDTO = ChampionshipMemberDTO.builder()
                .id(championshipMember.getId())
                .rank(4)
                .extra("New Test Extra")
                .build();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json, typeOfT, context) -> new Date(json.getAsJsonPrimitive().getAsLong()))
                .registerTypeAdapter(Date.class, (JsonSerializer<Date>) (date, type, jsonSerializationContext) -> new JsonPrimitive(date.getTime()))
                .create();

        this.mockMvc.perform(MockMvcRequestBuilders.put("/championship/member")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(gson.toJson(championshipMemberDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", is(championshipMember.getId().intValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.rank", is(championshipMemberDTO.getRank())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.extra", is(championshipMemberDTO.getExtra())));

        championshipMember = this.championshipMemberRepository
                .findById(championshipMember.getId()).orElseThrow(ChampionshipMemberNotFoundException::new);

        assertEquals(championshipMemberDTO.getExtra(), championshipMember.getExtra());
        assertEquals(championshipMemberDTO.getRank(), championshipMember.getRank());
    }

    @Test
    public void member_can_be_removed_from_championship() throws Exception {
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
        championshipMember = this.championshipMemberRepository.save(championshipMember);

        this.mockMvc.perform(MockMvcRequestBuilders.delete("/championship/member/" + championshipMember.getId()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        member = this.memberRepository.findById(member.getId()).orElseThrow(MemberNotFoundException::new);
        championship = this.championshipRepository.findById(championship.getId()).orElseThrow(ChampionshipMemberNotFoundException::new);

        assertFalse(this.championshipMemberRepository.findById(championshipMember.getId()).isPresent());
        assertEquals(0, member.getChampionships().size());
        assertEquals(0, championship.getMembers().size());

        this.mockMvc.perform(MockMvcRequestBuilders.delete("/championship/member/" + championshipMember.getId()))
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
                .accountHolder("Max Mustermann")
                .build();
    }
}
