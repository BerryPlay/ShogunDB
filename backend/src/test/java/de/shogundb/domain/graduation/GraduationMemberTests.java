package de.shogundb.domain.graduation;

import de.shogundb.TestHelper;
import de.shogundb.domain.contributionClass.ContributionClassRepository;
import de.shogundb.domain.discipline.Discipline;
import de.shogundb.domain.discipline.DisciplineRepository;
import de.shogundb.domain.exam.Exam;
import de.shogundb.domain.exam.ExamRepository;
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

import java.time.LocalDate;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
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
public class GraduationMemberTests {
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
    public void all_graduation_member_links_can_be_called() throws Exception {
        // create member
        Member member = TestHelper.createTestMember(contributionClassRepository);

        // create a discipline and a graduation
        Discipline discipline = disciplineRepository.save(Discipline.builder().name("Discipline").build());
        Graduation graduation = graduationRepository.save(TestHelper.createTestGraduation());

        discipline.getGraduations().add(graduation);

        // create an exam and persons
        Exam exam = examRepository.save(Exam.builder().date(LocalDate.parse("2018-01-02")).build());
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
        exam.getGraduationMembers().add(graduationMember);
        graduation.getGraduationMembers().add(graduationMember);
        member.getGraduations().add(graduationMember);

        graduationMember = graduationMemberRepository.save(graduationMember);

        mockMvc.perform(get("/graduationMember"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(graduationMember.getId().intValue()))
                .andExpect(jsonPath("$[0].graduation.id").value(is(graduation.getId().intValue())));
    }

    @Test
    public void graduation_member_link_can_be_added() throws Exception {
        // create member
        Member member = memberRepository.save(TestHelper.createTestMember(contributionClassRepository));

        // create a discipline and a graduation
        Discipline discipline = disciplineRepository.save(Discipline.builder().name("Discipline").build());
        Graduation graduation = graduationRepository.save(TestHelper.createTestGraduation());

        discipline.getGraduations().add(graduation);

        // create an exam and persons
        Exam exam = examRepository.save(Exam.builder().date(LocalDate.parse("2018-01-02")).build());
        Person examiner1 = personRepository.save(Person.builder().name("Test Examiner 1").build());
        Person examiner2 = personRepository.save(Person.builder().name("Test Examiner 2").build());

        exam.getExaminers().add(examiner1);
        exam.getExaminers().add(examiner2);
        examiner1.getExams().add(exam);
        examiner2.getExams().add(exam);

        exam = examRepository.save(exam);

        var graduationMember = GraduationMemberRegisterDTO.builder()
                .memberId(member.getId())
                .graduationId(graduation.getId())
                .examId(exam.getId())
                .build();

        mockMvc.perform(post("/graduationMember")
                .contentType(APPLICATION_JSON_UTF8)
                .content(TestHelper.toJson(graduationMember)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.graduation.id").value(is(graduation.getId().intValue())));

        // update the entities
        var updatedMember = memberRepository.findById(member.getId()).orElseThrow();
        var updatedGraduation = graduationRepository.findById(graduation.getId()).orElseThrow();
        var updatedExam = examRepository.findById(exam.getId()).orElseThrow();

        var newGraduationMember = graduationMemberRepository.findAll().iterator().next();

        // check the member - graduation member link
        assertEquals(1, updatedMember.getGraduations().size());
        assertEquals(newGraduationMember, updatedMember.getGraduations().get(0));
        assertEquals(updatedMember, newGraduationMember.getMember());

        // check the graduation - graduation member link
        assertEquals(1, updatedGraduation.getGraduationMembers().size());
        assertEquals(newGraduationMember, updatedGraduation.getGraduationMembers().get(0));
        assertEquals(updatedGraduation, newGraduationMember.getGraduation());

        // check the exam - graduation member link
        assertEquals(1, updatedExam.getGraduationMembers().size());
        assertEquals(newGraduationMember, updatedExam.getGraduationMembers().get(0));
        assertEquals(updatedExam, newGraduationMember.getExam());

        // destructive test with non set exam id
        graduationMember.setExamId(null);
        mockMvc.perform(post("/graduationMember")
                .contentType(APPLICATION_JSON_UTF8)
                .content(TestHelper.toJson(graduationMember)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void graduation_member_link_can_be_removed() throws Exception {
        // create member
        Member member = TestHelper.createTestMember(contributionClassRepository);

        // create a discipline and a graduation
        Discipline discipline = disciplineRepository.save(Discipline.builder().name("Discipline").build());
        Graduation graduation = graduationRepository.save(TestHelper.createTestGraduation());

        discipline.getGraduations().add(graduation);

        // create an exam and persons
        Exam exam = examRepository.save(Exam.builder().date(LocalDate.parse("2018-01-02")).build());
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
        exam.getGraduationMembers().add(graduationMember);
        graduation.getGraduationMembers().add(graduationMember);
        member.getGraduations().add(graduationMember);

        graduationMember = graduationMemberRepository.save(graduationMember);

        mockMvc.perform(delete("/graduationMember/" + graduationMember.getId()))
                .andExpect(status().isNoContent());

        // update the entities
        var updatedMember = memberRepository.findById(member.getId()).orElseThrow();
        var updatedGraduation = graduationRepository.findById(graduation.getId()).orElseThrow();
        var updatedExam = examRepository.findById(exam.getId()).orElseThrow();

        // check if the graduation member link was removed successfully
        assertFalse(graduationMemberRepository.existsById(graduationMember.getId()));

        // check if the links are removed from the other entities
        assertEquals(0, updatedMember.getGraduations().size());
        assertEquals(0, updatedGraduation.getGraduationMembers().size());
        assertEquals(0, updatedExam.getGraduationMembers().size());

        // destructive testing with not existing id
        mockMvc.perform(delete("/graduationMember/-1")).andExpect(status().isNotFound());
    }

    @Test
    public void graduation_member_link_can_be_called_by_id() throws Exception {
        // create member
        Member member = TestHelper.createTestMember(contributionClassRepository);

        // create a discipline and a graduation
        Discipline discipline = disciplineRepository.save(Discipline.builder().name("Discipline").build());
        Graduation graduation = graduationRepository.save(TestHelper.createTestGraduation());

        discipline.getGraduations().add(graduation);

        // create an exam and persons
        Exam exam = examRepository.save(Exam.builder().date(LocalDate.parse("2018-01-02")).build());
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
        exam.getGraduationMembers().add(graduationMember);
        graduation.getGraduationMembers().add(graduationMember);
        member.getGraduations().add(graduationMember);

        graduationMember = graduationMemberRepository.save(graduationMember);

        mockMvc.perform(get("/graduationMember/" + graduationMember.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(graduationMember.getId().intValue()))
                .andExpect(jsonPath("$.graduation.id").value(is(graduation.getId().intValue())));

        // destructive testing with a non existing id
        mockMvc.perform(get("/graduationMember/-1"))
                .andExpect(status().isNotFound());
    }
}
