package de.shogundb.domain.discipline;

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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles(profiles = "test")
@AutoConfigureTestDatabase
@Transactional
public class DisciplineMemberControllerTests {
    @Autowired
    private DisciplineRepository disciplineRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    public void all_members_of_discipline_can_be_called() throws Exception {
        Member member = this.memberRepository.save(this.createTestMember());
        Discipline discipline = this.disciplineRepository.save(Discipline.builder()
                .name("Test Discipline")
                .members((List<Member>) this.memberRepository.findAll())
                .build());
        member.getDisciplines().add(discipline);
        member = this.memberRepository.save(member);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/discipline/member/" + discipline.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", is(member.getId().intValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].disciplines", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].disciplines[0].id", is(discipline.getId().intValue())));

    }

    @Test
    public void member_can_be_added_to_discipline() throws Exception {
        Member member = this.memberRepository.save(this.createTestMember());
        Discipline discipline = this.disciplineRepository.save(Discipline.builder()
                .name("Test Discipline")
                .build());

        this.mockMvc.perform(MockMvcRequestBuilders
                .post("/discipline/member/" + discipline.getId() + "/" + member.getId()))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", is(member.getId().intValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.disciplines", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.disciplines[0].id", is(discipline.getId().intValue())));

        member = this.memberRepository.findById(member.getId()).orElseThrow(MemberNotFoundException::new);
        discipline = this.disciplineRepository.findById(discipline.getId()).orElseThrow(DisciplineNotFoundException::new);

        assertEquals(member.getDisciplines().get(0), discipline);
        assertEquals(discipline.getMembers().get(0), member);
    }

    @Test
    public void member_can_be_removed_from_discipline() throws Exception {
        Member member1 = this.memberRepository.save(this.createTestMember());
        Member member2 = this.memberRepository.save(this.createTestMember());
        Discipline discipline = this.disciplineRepository.save(Discipline.builder()
                .name("Test Discipline")
                .build());

        member1.getDisciplines().add(discipline);
        member2.getDisciplines().add(discipline);
        discipline.getMembers().add(member1);
        discipline.getMembers().add(member2);

        discipline = this.disciplineRepository.save(discipline);

        this.mockMvc.perform(MockMvcRequestBuilders
                .delete("/discipline/member/" + discipline.getId() + "/" + member2.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", is(member1.getId().intValue())));

        member1 = this.memberRepository.findById(member1.getId()).orElseThrow(MemberNotFoundException::new);
        member2 = this.memberRepository.findById(member2.getId()).orElseThrow(MemberNotFoundException::new);

        assertTrue(member1.getDisciplines().contains(discipline));
        assertTrue(discipline.getMembers().contains(member1));
        assertFalse(member2.getDisciplines().contains(discipline));
        assertFalse(discipline.getMembers().contains(member2));

        this.mockMvc.perform(MockMvcRequestBuilders
                .delete("/discipline/member/" + discipline.getId() + "/" + member2.getId()))
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
