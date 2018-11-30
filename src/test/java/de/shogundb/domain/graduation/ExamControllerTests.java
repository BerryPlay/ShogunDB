package de.shogundb.domain.graduation;

import de.shogundb.TestHelper;
import de.shogundb.domain.contributionClass.ContributionClassRepository;
import de.shogundb.domain.discipline.Discipline;
import de.shogundb.domain.discipline.DisciplineRepository;
import de.shogundb.domain.member.Member;
import de.shogundb.domain.member.MemberRepository;
import de.shogundb.domain.person.Person;
import de.shogundb.domain.person.PersonRepository;
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

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles(profiles = "test")
@AutoConfigureTestDatabase
@Transactional
public class ExamControllerTests {
    private MockMvc mockMvc;

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private DisciplineRepository disciplineRepository;

    @Autowired
    private GraduationRepository graduationRepository;

    @Autowired
    private GraduationMemberRepository graduationMemberRepository;

    @Autowired
    private ContributionClassRepository contributionClassRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void all_exams_can_be_called() throws Exception {
        // create member
        Member member = TestHelper.createTestMember(contributionClassRepository);

        // create a discipline and a graduation
        Discipline discipline = disciplineRepository.save(Discipline.builder().name("Discipline").build());
        Graduation graduation = graduationRepository.save(Graduation.builder()
                .name("Test Graduation")
                .discipline(discipline)
                .examConditions("")
                .highlightConditions("")
                .color("RED")
                .build());

        discipline.getGraduations().add(graduation);

        // create an exam and persons
        Exam exam = examRepository.save(Exam.builder().date(new Date()).build());
        Person examiner1 = personRepository.save(Person.builder().name("Test Examiner 1").build());
        Person examiner2 = personRepository.save(Person.builder().name("Test Examiner 2").build());

        exam.getExaminers().add(examiner1);
        exam.getExaminers().add(examiner2);
        examiner1.getExams().add(exam);
        examiner2.getExams().add(exam);

        // create a graduation member connection
        GraduationMember graduationMember = GraduationMember.builder()
                .exam(exam)
                .graduation(graduation)
                .member(member)
                .build();
        exam.getGraduationMember().add(graduationMember);
        graduation.getGraduationMembers().add(graduationMember);
        member.getGraduations().add(graduationMember);

        graduationMember = graduationMemberRepository.save(graduationMember);

        mockMvc.perform(get("/exam"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(exam.getId().intValue()))
                .andExpect(jsonPath("$[0].date").value(exam.getDate().getTime()))
                .andExpect(jsonPath("$[0].graduationMember").value(hasSize(1)))
                .andExpect(jsonPath("$[0].graduationMember[0].id")
                        .value(graduationMember.getId().intValue()))
                .andExpect(jsonPath("$[0].examiners").value(hasSize(2)))
                .andExpect(jsonPath("$[0].examiners[0].id").value(examiner1.getId().intValue()))
                .andExpect(jsonPath("$[0].examiners[1].id").value(examiner2.getId().intValue()));
    }
}
