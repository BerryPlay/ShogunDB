package de.shogundb.domain.championship;

import de.shogundb.domain.contributionClass.ContributionClassRepository;
import de.shogundb.domain.member.Member;
import de.shogundb.domain.member.MemberNotFoundException;
import de.shogundb.domain.member.MemberRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static de.shogundb.TestHelper.createTestMember;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles(profiles = "developement")
@AutoConfigureTestDatabase
@Transactional
public class ChampionshipMemberTests {
    @Autowired
    private ChampionshipRepository championshipRepository;

    @Autowired
    private ChampionshipMemberRepository championshipMemberRepository;

    @Autowired
    private ContributionClassRepository contributionClassRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    public void championship_member_tests() throws MemberNotFoundException {
        Member member = this.memberRepository.save(createTestMember(contributionClassRepository));

        Championship championship = this.championshipRepository.save(Championship.builder()
                .name("Test Championship")
                .date(new Date(1514764800000L))
                .build());

        ChampionshipMember championshipMember = ChampionshipMember.builder()
                .championship(championship)
                .member(member)
                .extra("Gew. Klasse bis 50kg")
                .rank(2)
                .build();

        member.getChampionships().add(championshipMember);
        championship.getMembers().add(championshipMember);

        this.championshipMemberRepository.save(championshipMember);

        member = this.memberRepository.findById(member.getId()).orElseThrow(MemberNotFoundException::new);
        championship = this.championshipRepository.findById(championship.getId()).orElseThrow(MemberNotFoundException::new);
        championshipMember = this.championshipMemberRepository.findById(championshipMember.getId())
                .orElseThrow(MemberNotFoundException::new);

        assertTrue(member.getChampionships().contains(championshipMember));
        assertTrue(championship.getMembers().contains(championshipMember));

        assertEquals(member, member.getChampionships().get(0).getChampionship().getMembers().get(0).getMember());
        assertEquals(championshipMember.getExtra(), member.getChampionships().get(0).getExtra());
        assertEquals(championshipMember.getExtra(), championship.getMembers().get(0).getExtra());
    }
}
