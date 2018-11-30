package de.shogundb.domain.championship;

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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static de.shogundb.TestHelper.createTestMember;
import static de.shogundb.TestHelper.toJson;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.IsNull.notNullValue;
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
public class ChampionshipControllerTests {
    @Autowired
    private ChampionshipRepository championshipRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ChampionshipMemberRepository championshipMemberRepository;

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
    public void all_championships_can_be_called() throws Exception {
        Championship championship = championshipRepository.save(Championship.builder()
                .name("Test Championship")
                .date(LocalDate.parse("2018-01-02"))
                .build());

        mockMvc.perform(get("/championship"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(is(championship.getId().intValue())))
                .andExpect(jsonPath("$[0].name").value(is(championship.getName())))
                .andExpect(jsonPath("$[0].date").value(is(championship.getDate().toString())));
    }

    @Test
    public void championship_can_be_added() throws Exception {
        Member member1 = memberRepository.save(createTestMember(contributionClassRepository));
        Member member2 = memberRepository.save(createTestMember(contributionClassRepository));

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
                .date(LocalDate.parse("2018-01-02"))
                .members(members)
                .build();

        mockMvc.perform(post("/championship")
                .contentType(APPLICATION_JSON_UTF8)
                .content(toJson(championshipRegisterDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(notNullValue()))
                .andExpect(jsonPath("$.name").value(is(championshipRegisterDTO.getName())))
                .andExpect(jsonPath("$.date").value(is(championshipRegisterDTO.getDate().toString())));

        assertEquals(1L, championshipRepository.count());

        championshipRepository.findAll().forEach(
                existing -> assertEquals(2, existing.getMembers().size()));

        assertEquals(2L, championshipMemberRepository.count());

        List<Member> memberList = new ArrayList<>();
        memberList.add(member1);
        memberList.add(member2);

        championshipMemberRepository.findAll().forEach(
                existing -> {
                    assertTrue(memberList.contains(existing.getMember()));
                    try {
                        championshipRepository.findById(existing.getChampionship().getId()).orElseThrow(ChampionshipNotFoundException::new);
                    } catch (ChampionshipNotFoundException e) {
                        fail();
                    }
                });
    }

    @Test
    public void championship_can_be_updated() throws Exception {
        Member member1 = memberRepository.save(createTestMember(contributionClassRepository));
        Member member2 = memberRepository.save(createTestMember(contributionClassRepository));

        Championship championship = championshipRepository.save(Championship.builder()
                .name("Test Championship")
                .date(LocalDate.parse("2018-01-02"))
                .build());

        ChampionshipMember championshipMember = ChampionshipMember.builder()
                .member(member1)
                .championship(championship)
                .rank(1)
                .extra("Test Extra")
                .build();

        member1.getChampionships().add(championshipMember);
        championship.getMembers().add(championshipMember);

        championship = championshipRepository.save(championship);

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
                .date(LocalDate.parse("2018-01-02"))
                .members(members)
                .build();

        mockMvc.perform(put("/championship")
                .contentType(APPLICATION_JSON_UTF8)
                .content(toJson(championshipUpdateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(notNullValue()))
                .andExpect(jsonPath("$.name").value(is(championshipUpdateDTO.getName())))
                .andExpect(jsonPath("$.date").value(is(championshipUpdateDTO.getDate().toString())));

        member1 = memberRepository.findById(member1.getId()).orElseThrow(MemberNotFoundException::new);
        member2 = memberRepository.findById(member2.getId()).orElseThrow(MemberNotFoundException::new);

        assertTrue(member1.getChampionships().isEmpty());
        assertFalse(member2.getChampionships().isEmpty());
        assertEquals(member2, member2.getChampionships().get(0).getMember());

        championship = championshipRepository.findById(championship.getId())
                .orElseThrow(ChampionshipNotFoundException::new);

        assertEquals(1, championship.getMembers().size());
        assertEquals(member2, championship.getMembers().get(0).getMember());
        assertEquals(championship, member2.getChampionships().get(0).getChampionship());
        assertEquals(championshipMemberRegisterDTO.getExtra(), member2.getChampionships().get(0).getExtra());
        assertEquals(championshipMemberRegisterDTO.getRank(), member2.getChampionships().get(0).getRank());
    }

    @Test
    public void championship_can_be_deleted() throws Exception {
        Member member = memberRepository.save(createTestMember(contributionClassRepository));

        Championship championship = championshipRepository.save(Championship.builder()
                .name("Test Championship")
                .date(LocalDate.parse("2018-01-02"))
                .build());

        ChampionshipMember championshipMember = ChampionshipMember.builder()
                .member(member)
                .championship(championship)
                .rank(1)
                .extra("Test Extra")
                .build();

        member.getChampionships().add(championshipMember);
        championship.getMembers().add(championshipMember);

        championship = championshipRepository.save(championship);

        mockMvc.perform(delete("/championship/" + championship.getId()))
                .andExpect(status().isNoContent());

        member = memberRepository.findById(member.getId()).orElseThrow(MemberNotFoundException::new);

        assertTrue(member.getChampionships().isEmpty());
        assertFalse(championshipRepository.findById(championship.getId()).isPresent());
        assertEquals(0, championshipMemberRepository.count());

        mockMvc.perform(delete("/championship/-1"))
                .andExpect(status().isConflict());
    }

    @Test
    public void championship_can_be_called_by_id() throws Exception {
        Championship championship = championshipRepository.save(Championship.builder()
                .name("Test Championship")
                .date(LocalDate.parse("2018-01-02"))
                .build());

        mockMvc.perform(get("/championship/" + championship.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(championship.getId().intValue())))
                .andExpect(jsonPath("$.name", is(championship.getName())))
                .andExpect(jsonPath("$.date", is(championship.getDate().toString())));

        mockMvc.perform(get("/championship/-1"))
                .andExpect(status().isConflict());
    }
}
