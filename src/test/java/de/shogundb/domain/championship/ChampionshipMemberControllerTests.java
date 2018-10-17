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

import java.util.Date;

import static de.shogundb.TestHelper.createTestMember;
import static de.shogundb.TestHelper.toJson;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    private ContributionClassRepository contributionClassRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void all_members_of_championships_can_be_called() throws Exception {
        Member member = memberRepository.save(createTestMember(contributionClassRepository));

        Championship championship = championshipRepository.save(Championship.builder()
                .name("Test Championship")
                .date(new Date(1514764800000L))
                .build());

        ChampionshipMember championshipMember = championshipMemberRepository.save(ChampionshipMember.builder()
                .championship(championship)
                .member(member)
                .extra("Gew. Klasse bis 50kg")
                .rank(2)
                .build());

        member.getChampionships().add(championshipMember);
        championship.getMembers().add(championshipMember);

        championshipMember = championshipMemberRepository.save(championshipMember);

        mockMvc.perform(get("/championship/member/" + championship.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(is(championshipMember.getId().intValue())))
                .andExpect(jsonPath("$[0].rank").value(is(championshipMember.getRank())))
                .andExpect(jsonPath("$[0].extra").value(is(championshipMember.getExtra())))
                .andExpect(jsonPath("$[0].member.id").value(is(member.getId().intValue())));
    }

    @Test
    public void member_can_be_added_to_championship() throws Exception {
        Member member = memberRepository.save(createTestMember(contributionClassRepository));
        Championship championship = championshipRepository.save(Championship.builder()
                .name("Test Championship")
                .date(new Date(1514764800000L))
                .build());

        ChampionshipMemberRegisterDTO championshipMemberRegisterDTO = ChampionshipMemberRegisterDTO.builder()
                .memberId(member.getId())
                .rank(2)
                .extra("Test Extra")
                .build();

        mockMvc.perform(post("/championship/member/" + championship.getId())
                .contentType(APPLICATION_JSON_UTF8)
                .content(toJson(championshipMemberRegisterDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.championship.id").value(is(championship.getId().intValue())))
                .andExpect(jsonPath("$.rank").value(is(championshipMemberRegisterDTO.getRank())))
                .andExpect(jsonPath("$.extra").value(is(championshipMemberRegisterDTO.getExtra())));

        member = memberRepository.findById(member.getId()).orElseThrow(MemberNotFoundException::new);
        championship = championshipRepository.findById(championship.getId()).orElseThrow(ChampionshipNotFoundException::new);

        assertEquals(member, championship.getMembers().get(0).getMember());
        assertEquals(championship, member.getChampionships().get(0).getChampionship());

        assertEquals(championshipMemberRegisterDTO.getRank(), member.getChampionships().get(0).getRank());
        assertEquals(championshipMemberRegisterDTO.getExtra(), championship.getMembers().get(0).getExtra());

        mockMvc.perform(post("/championship/member/" + championship.getId())
                .contentType(APPLICATION_JSON_UTF8)
                .content(toJson(championshipMemberRegisterDTO)))
                .andExpect(status().isConflict());
    }


    @Test
    public void championship_member_can_be_updated() throws Exception {
        Member member = memberRepository.save(createTestMember(contributionClassRepository));
        Championship championship = championshipRepository.save(Championship.builder()
                .name("Test Championship")
                .date(new Date(1514764800000L))
                .build());

        ChampionshipMember championshipMember = championshipMemberRepository.save(ChampionshipMember.builder()
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

        mockMvc.perform(put("/championship/member")
                .contentType(APPLICATION_JSON_UTF8)
                .content(toJson(championshipMemberDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(is(championshipMember.getId().intValue())))
                .andExpect(jsonPath("$.rank").value(is(championshipMemberDTO.getRank())))
                .andExpect(jsonPath("$.extra").value(is(championshipMemberDTO.getExtra())));

        championshipMember = championshipMemberRepository
                .findById(championshipMember.getId()).orElseThrow(ChampionshipMemberNotFoundException::new);

        assertEquals(championshipMemberDTO.getExtra(), championshipMember.getExtra());
        assertEquals(championshipMemberDTO.getRank(), championshipMember.getRank());
    }

    @Test
    public void member_can_be_removed_from_championship() throws Exception {
        Member member = memberRepository.save(createTestMember(contributionClassRepository));
        Championship championship = championshipRepository.save(Championship.builder()
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
        championshipMember = championshipMemberRepository.save(championshipMember);

        mockMvc.perform(delete("/championship/member/" + championshipMember.getId()))
                .andExpect(status().isNoContent());

        member = memberRepository.findById(member.getId()).orElseThrow(MemberNotFoundException::new);
        championship = championshipRepository.findById(championship.getId()).orElseThrow(ChampionshipMemberNotFoundException::new);

        assertFalse(championshipMemberRepository.findById(championshipMember.getId()).isPresent());
        assertEquals(0, member.getChampionships().size());
        assertEquals(0, championship.getMembers().size());

        mockMvc.perform(delete("/championship/member/" + championshipMember.getId()))
                .andExpect(status().isConflict());
    }
}
