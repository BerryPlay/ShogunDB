package de.shogundb.domain.discipline;

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

import java.util.List;

import static de.shogundb.TestHelper.createTestMember;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles(profiles = "test")
@AutoConfigureTestDatabase
@Transactional
public class DisciplineMemberControllerTests {
    @Autowired
    private ContributionClassRepository contributionClassRepository;

    @Autowired
    private DisciplineRepository disciplineRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void all_members_of_discipline_can_be_called() throws Exception {
        Member member = memberRepository.save(createTestMember(contributionClassRepository));
        Discipline discipline = disciplineRepository.save(Discipline.builder()
                .name("Test Discipline")
                .members((List<Member>) memberRepository.findAll())
                .build());
        member.getDisciplines().add(discipline);
        member = memberRepository.save(member);

        mockMvc.perform(get("/discipline/member/" + discipline.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(is(member.getId().intValue())))
                .andExpect(jsonPath("$[0].disciplines").value(hasSize(1)))
                .andExpect(jsonPath("$[0].disciplines[0].id").value(is(discipline.getId().intValue())));

    }

    @Test
    public void member_can_be_added_to_discipline() throws Exception {
        Member member = memberRepository.save(createTestMember(contributionClassRepository));
        Discipline discipline = disciplineRepository.save(Discipline.builder()
                .name("Test Discipline")
                .build());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/discipline/member/" + discipline.getId() + "/" + member.getId()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(is(member.getId().intValue())))
                .andExpect(jsonPath("$.disciplines").value(hasSize(1)))
                .andExpect(jsonPath("$.disciplines[0].id").value(is(discipline.getId().intValue())));

        member = memberRepository.findById(member.getId()).orElseThrow(MemberNotFoundException::new);
        discipline = disciplineRepository.findById(discipline.getId()).orElseThrow(DisciplineNotFoundException::new);

        assertEquals(member.getDisciplines().get(0), discipline);
        assertEquals(discipline.getMembers().get(0), member);
    }

    @Test
    public void member_can_be_removed_from_discipline() throws Exception {
        Member member1 = memberRepository.save(createTestMember(contributionClassRepository));
        Member member2 = memberRepository.save(createTestMember(contributionClassRepository));
        Discipline discipline = disciplineRepository.save(Discipline.builder()
                .name("Test Discipline")
                .build());

        member1.getDisciplines().add(discipline);
        member2.getDisciplines().add(discipline);
        discipline.getMembers().add(member1);
        discipline.getMembers().add(member2);

        discipline = disciplineRepository.save(discipline);

        mockMvc.perform(MockMvcRequestBuilders
                .delete("/discipline/member/" + discipline.getId() + "/" + member2.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(is(member1.getId().intValue())));

        member1 = memberRepository.findById(member1.getId()).orElseThrow(MemberNotFoundException::new);
        member2 = memberRepository.findById(member2.getId()).orElseThrow(MemberNotFoundException::new);

        assertTrue(member1.getDisciplines().contains(discipline));
        assertTrue(discipline.getMembers().contains(member1));
        assertFalse(member2.getDisciplines().contains(discipline));
        assertFalse(discipline.getMembers().contains(member2));

        mockMvc.perform(MockMvcRequestBuilders
                .delete("/discipline/member/" + discipline.getId() + "/" + member2.getId()))
                .andExpect(status().isConflict());

    }
}
