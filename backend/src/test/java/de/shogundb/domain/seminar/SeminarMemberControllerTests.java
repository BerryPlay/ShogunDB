package de.shogundb.domain.seminar;

import de.shogundb.domain.contributionClass.ContributionClassRepository;
import de.shogundb.domain.member.Member;
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

import static de.shogundb.TestHelper.createTestMember;
import static de.shogundb.TestHelper.createTestSeminar;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles(profiles = "test")
@AutoConfigureTestDatabase
@Transactional
public class SeminarMemberControllerTests {
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ContributionClassRepository contributionClassRepository;

    @Autowired
    private SeminarRepository seminarRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void all_members_of_a_seminar_can_be_called() throws Exception {
        // create seminar
        Seminar seminar = createTestSeminar();

        // create members
        Member member1 = createTestMember(contributionClassRepository);
        Member member2 = createTestMember(contributionClassRepository);

        // link the seminar and the members
        seminar.getMembers().add(member1);
        seminar.getMembers().add(member2);
        member1.getSeminars().add(seminar);
        member2.getSeminars().add(seminar);

        seminar = seminarRepository.save(seminar);

        mockMvc.perform(get("/seminar/member/" + seminar.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(is(member1.getId().intValue())))
                .andExpect(jsonPath("$[1].id").value(is(member2.getId().intValue())));


        mockMvc.perform(get("/seminar/member/-1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void member_can_be_added_to_seminar() throws Exception {
        // create seminar
        Seminar seminar = createTestSeminar();

        // create members
        Member member1 = memberRepository.save(createTestMember(contributionClassRepository));
        Member member2 = memberRepository.save(createTestMember(contributionClassRepository));

        // link the seminar and the referents
        seminar.getMembers().add(member1);
        member1.getSeminars().add(seminar);

        seminar = seminarRepository.save(seminar);

        mockMvc.perform(post("/seminar/member/" + seminar.getId() + "/" + member2.getId()))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/seminar/member/" + seminar.getId() + "/" + member2.getId()))
                .andExpect(status().isConflict());

        // update the previous entities
        seminar = seminarRepository.findById(seminar.getId()).orElseThrow();
        member1 = memberRepository.findById(member1.getId()).orElseThrow();
        member2 = memberRepository.findById(member2.getId()).orElseThrow();

        assertEquals(2, seminar.getMembers().size());
        assertEquals(1, member1.getSeminars().size());
        assertEquals(1, member2.getSeminars().size());

        assertEquals(seminar, member1.getSeminars().get(0));
        assertEquals(seminar, member2.getSeminars().get(0));

        assertEquals(member1, seminar.getMembers().get(0));
        assertEquals(member2, seminar.getMembers().get(1));
    }

    @Test
    public void member_can_be_removed_from_seminar() throws Exception {
        // create seminar
        Seminar seminar = createTestSeminar();

        // create members
        Member member1 = createTestMember(contributionClassRepository);
        Member member2 = createTestMember(contributionClassRepository);

        // link the seminar and the members
        seminar.getMembers().add(member1);
        seminar.getMembers().add(member2);
        member1.getSeminars().add(seminar);
        member2.getSeminars().add(seminar);

        seminar = seminarRepository.save(seminar);

        mockMvc.perform(delete("/seminar/member/" + seminar.getId() + "/" + member1.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/seminar/member/" + seminar.getId() + "/" + member1.getId()))
                .andExpect(status().isNotFound());

        // update the previous entities
        seminar = seminarRepository.findById(seminar.getId()).orElseThrow();
        member1 = memberRepository.findById(member1.getId()).orElseThrow();
        member2 = memberRepository.findById(member2.getId()).orElseThrow();

        assertEquals(1, seminar.getMembers().size());
        assertEquals(0, member1.getSeminars().size());
        assertEquals(1, member2.getSeminars().size());

        assertEquals(seminar, member2.getSeminars().get(0));
        assertEquals(member2, seminar.getMembers().get(0));
    }
}
