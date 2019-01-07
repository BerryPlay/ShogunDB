package de.shogundb.domain.contributionClass;

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

import java.time.LocalDate;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles(profiles = "test")
@AutoConfigureTestDatabase
@Transactional
public class ContributionClassMemberControllerTests {

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
    public void all_members_of_a_contribution_class_can_be_called() throws Exception {
        Member member = memberRepository.save(createTestMember());

        ContributionClass contributionClass = contributionClassRepository.save(
                ContributionClass.builder()
                        .name("Test Contribution Class")
                        .baseContribution(30.5)
                        .additionalContribution(5)
                        .build());

        member.setContributionClass(contributionClass);
        contributionClass.getMembers().add(member);
        contributionClass = contributionClassRepository.save(contributionClass);

        mockMvc.perform(MockMvcRequestBuilders.get("/contributionClass/member/" + contributionClass.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", is(member.getId().intValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].contributionClass.id", is(contributionClass.getId().intValue())));
    }

    @Test
    public void member_can_be_added_to_contribution_class() throws Exception {
        Member member = memberRepository.save(createTestMember());

        ContributionClass contributionClass = contributionClassRepository.save(
                ContributionClass.builder()
                        .name("Test Contribution Class")
                        .baseContribution(30.5)
                        .additionalContribution(5)
                        .build());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/contributionClass/member/" + contributionClass.getId() + "/" + member.getId()))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", is(member.getId().intValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contributionClass.id", is(member.getContributionClass().getId().intValue())));

        contributionClass = contributionClassRepository.findById(contributionClass.getId())
                .orElseThrow(ContributionClassNotFoundException::new);

        member = memberRepository.findById(member.getId()).orElseThrow(MemberNotFoundException::new);

        assertEquals(member.getContributionClass(), contributionClass);
        assertTrue(contributionClass.getMembers().contains(member));
    }

    @Test
    public void member_can_removed_from_contribution_class() throws Exception {
        Member member = memberRepository.save(createTestMember());

        ContributionClass contributionClass = contributionClassRepository.save(
                ContributionClass.builder()
                        .name("Test Contribution Class")
                        .baseContribution(30.5)
                        .additionalContribution(5)
                        .build());

        member.setContributionClass(contributionClass);
        contributionClass.getMembers().add(member);
        contributionClass = contributionClassRepository.save(contributionClass);

        mockMvc.perform(MockMvcRequestBuilders
                .delete("/contributionClass/member/" + contributionClass.getId() + "/" + member.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", is(member.getId().intValue())));

        contributionClass = contributionClassRepository.findById(contributionClass.getId())
                .orElseThrow(ContributionClassNotFoundException::new);

        member = memberRepository.findById(member.getId()).orElseThrow(MemberNotFoundException::new);

        assertNull(member.getContributionClass());
        assertFalse(contributionClass.getMembers().contains(member));

        mockMvc.perform(MockMvcRequestBuilders
                .delete("/contributionClass/member/" + contributionClass.getId() + "/" + member.getId()))
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
                .dateOfBirth(LocalDate.parse("1005-03-09"))
                .hasBudoPass(true)
                .budoPassDate(LocalDate.parse("2018-01-02"))
                .enteredDate(LocalDate.parse("2018-01-02"))
                .hasLeft(false)
                .leftDate(null)
                .isPassive(false)
                .accountHolder("Max Mustermann")
                .build();
    }
}
