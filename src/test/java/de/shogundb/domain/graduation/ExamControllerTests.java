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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.ArrayList;

import static de.shogundb.TestHelper.toJson;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
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
        exam.getGraduationMember().add(graduationMember);
        graduation.getGraduationMembers().add(graduationMember);
        member.getGraduations().add(graduationMember);

        graduationMember = graduationMemberRepository.save(graduationMember);

        mockMvc.perform(get("/exam"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(exam.getId().intValue()))
                .andExpect(jsonPath("$[0].date").value(exam.getDate().toString()))
                .andExpect(jsonPath("$[0].graduationMember").value(hasSize(1)))
                .andExpect(jsonPath("$[0].graduationMember[0].id")
                        .value(graduationMember.getId().intValue()))
                .andExpect(jsonPath("$[0].examiners").value(hasSize(2)))
                .andExpect(jsonPath("$[0].examiners[0].id").value(examiner1.getId().intValue()))
                .andExpect(jsonPath("$[0].examiners[1].id").value(examiner2.getId().intValue()));
    }

    @Test
    public void exam_can_be_added() throws Exception {
        // create members
        var member1 = memberRepository.save(TestHelper.createTestMember(contributionClassRepository));
        var member2 = memberRepository.save(TestHelper.createTestMember(contributionClassRepository));

        // create examiners
        var examiner1 = personRepository.save(Person.builder().name("Examiner 1").build());
        var examiner2 = personRepository.save(Person.builder().name("Examiner 2").build());

        // create a discipline and a linked graduation
        var discipline = disciplineRepository.save(Discipline.builder()
                .name("Test Discipline")
                .members(new ArrayList<>() {{
                    add(member1);
                    add(member2);
                }})
                .build());

        var graduation = TestHelper.createTestGraduation();
        discipline.getGraduations().add(graduation);
        graduation.setDiscipline(discipline);
        var savedGraduation = graduationRepository.save(graduation);

        var newExam = ExamRegisterDTO.builder()
                .date(LocalDate.parse("2018-01-02"))
                .examiners(new ArrayList<>() {{
                    add(examiner1.getId());
                    add(examiner2.getId());
                }})
                .graduationMember(new ArrayList<>() {{
                    add(GraduationMemberRegisterDTO.builder()
                            .memberId(member1.getId())
                            .graduationId(savedGraduation.getId())
                            .build());
                    add(GraduationMemberRegisterDTO.builder()
                            .memberId(member2.getId())
                            .graduationId(savedGraduation.getId())
                            .build());
                }})
                .build();

        // perform a mock post request
        mockMvc.perform(post("/exam")
                .contentType(APPLICATION_JSON_UTF8)
                .content(toJson(newExam)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.date").value(is(newExam.getDate().toString())))
                .andExpect(jsonPath("$.examiners").value(hasSize(2)))
                .andExpect(jsonPath("$.examiners[0].id").value(is(examiner1.getId().intValue())))
                .andExpect(jsonPath("$.examiners[0].name").value(is(examiner1.getName())))
                .andExpect(jsonPath("$.examiners[1].id").value(is(examiner2.getId().intValue())))
                .andExpect(jsonPath("$.examiners[1].name").value(is(examiner2.getName())))
                .andExpect(jsonPath("$.graduationMember").value(hasSize(2)));

        // fetch the changed entities from the database
        var updatedMember1 = memberRepository.findById(member1.getId()).orElseThrow();
        var updatedMember2 = memberRepository.findById(member2.getId()).orElseThrow();

        var updatedDiscipline = disciplineRepository.findById(discipline.getId()).orElseThrow();
        var updatedGraduation = graduationRepository.findById(graduation.getId()).orElseThrow();

        var createdExam = examRepository.findAll().iterator().next();

        // check if the exam exists
        assertNotNull(createdExam);

        assertEquals(1, updatedMember1.getGraduations().size());
        assertEquals(discipline, updatedMember1.getGraduations().get(0).getGraduation().getDiscipline());
        assertEquals(1, updatedMember2.getGraduations().size());
        assertEquals(discipline, updatedMember2.getGraduations().get(0).getGraduation().getDiscipline());

        assertEquals(2, updatedDiscipline.getGraduations().get(0).getGraduationMembers().size());
        assertEquals(member1, discipline.getGraduations().get(0).getGraduationMembers().get(0).getMember());
        assertEquals(member2, discipline.getGraduations().get(0).getGraduationMembers().get(1).getMember());

        assertEquals(discipline, updatedGraduation.getGraduationMembers().get(0).getGraduation().getDiscipline());

        assertEquals(createdExam, createdExam.getGraduationMember().get(0).getExam());
    }

    @Test
    public void exam_can_be_updated() throws Exception {
        // create member
        var member1 = memberRepository.save(TestHelper.createTestMember(contributionClassRepository));
        var member2 = memberRepository.save(TestHelper.createTestMember(contributionClassRepository));
        var member3 = memberRepository.save(TestHelper.createTestMember(contributionClassRepository));

        // create a discipline and a graduation
        var discipline = disciplineRepository.save(Discipline.builder().name("Discipline").build());
        var graduation = graduationRepository.save(TestHelper.createTestGraduation());

        discipline.getGraduations().add(graduation);

        // create an exam and persons
        var exam = examRepository.save(Exam.builder().date(LocalDate.parse("2018-01-02")).build());
        var examiner1 = personRepository.save(Person.builder().name("Test Examiner 1").build());
        var examiner2 = personRepository.save(Person.builder().name("Test Examiner 2").build());
        var examiner3 = personRepository.save(Person.builder().name("Test Examiner 3").build());

        exam.getExaminers().add(examiner1);
        exam.getExaminers().add(examiner2);
        examiner1.getExams().add(exam);
        examiner2.getExams().add(exam);

        // create two graduation member connections
        var graduationMember1 = GraduationMember.builder()
                .exam(exam)
                .graduation(graduation)
                .member(member1)
                .build();
        exam.getGraduationMember().add(graduationMember1);
        graduation.getGraduationMembers().add(graduationMember1);
        member1.getGraduations().add(graduationMember1);

        var graduationMember2 = GraduationMember.builder()
                .exam(exam)
                .graduation(graduation)
                .member(member2)
                .build();
        exam.getGraduationMember().add(graduationMember2);
        graduation.getGraduationMembers().add(graduationMember2);
        member2.getGraduations().add(graduationMember2);

        graduationMemberRepository.save(graduationMember1);
        graduationMemberRepository.save(graduationMember2);

        // create a exam update dto
        var examUpdateDTO = ExamUpdateDTO.builder()
                .id(exam.getId())
                .date(LocalDate.parse("2018-03-10"))
                .examiners(new ArrayList<>() {{
                    // an existing connection to keep
                    add(examiner2.getId());

                    // a new connection to add
                    add(examiner3.getId());
                }})
                .graduationMember(new ArrayList<>() {{
                    // an existing one to keep
                    add(GraduationMemberRegisterDTO.builder()
                            .graduationId(graduationMember2.getGraduation().getId())
                            .memberId(graduationMember2.getMember().getId())
                            .build());

                    // a new one to create
                    add(GraduationMemberRegisterDTO.builder()
                            .graduationId(graduation.getId())
                            .memberId(member3.getId())
                            .build());
                }})
                .build();

        mockMvc.perform(put("/exam")
                .contentType(APPLICATION_JSON_UTF8)
                .content(toJson(examUpdateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(exam.getId().intValue()))
                .andExpect(jsonPath("$.date").value(examUpdateDTO.getDate().toString()))
                .andExpect(jsonPath("$.examiners").value(hasSize(2)))
                .andExpect(jsonPath("$.examiners[0].id").value(is(examiner2.getId().intValue())))
                .andExpect(jsonPath("$.examiners[0].name").value(is(examiner2.getName())))
                .andExpect(jsonPath("$.examiners[1].id").value(is(examiner3.getId().intValue())))
                .andExpect(jsonPath("$.examiners[1].name").value(is(examiner3.getName())))
                .andExpect(jsonPath("$.graduationMember").value(hasSize(2)));

        // fetch the changed entities from the database
        var updatedMember1 = memberRepository.findById(member1.getId()).orElseThrow();
        var updatedMember2 = memberRepository.findById(member2.getId()).orElseThrow();
        var updatedMember3 = memberRepository.findById(member3.getId()).orElseThrow();

        var updatedGraduation = graduationRepository.findById(graduation.getId()).orElseThrow();

        var updatedExam = examRepository.findById(exam.getId()).orElseThrow();

        var updatedExaminer1 = personRepository.findById(examiner1.getId()).orElseThrow();
        var updatedExaminer2 = personRepository.findById(examiner2.getId()).orElseThrow();
        var updatedExaminer3 = personRepository.findById(examiner3.getId()).orElseThrow();

        // assert that the links between the members and the exams are correct
        assertEquals(0, updatedMember1.getGraduations().size());
        assertEquals(1, updatedMember2.getGraduations().size());
        assertEquals(1, updatedMember3.getGraduations().size());
        assertEquals(2, updatedExam.getGraduationMember().size());

        assertEquals(updatedMember2, updatedExam.getGraduationMember().get(0).getMember());
        assertEquals(updatedMember3, updatedExam.getGraduationMember().get(1).getMember());

        // assert that the links between the graduation and the exams are correct
        assertEquals(updatedGraduation, exam.getGraduationMember().get(0).getGraduation());
        assertEquals(exam, updatedGraduation.getGraduationMembers().get(0).getExam());

        // assert that the links between the examiner and the exams are correct
        assertEquals(0, updatedExaminer1.getExams().size());
        assertEquals(1, updatedExaminer2.getExams().size());
        assertEquals(1, updatedExaminer2.getExams().size());

        assertEquals(exam, updatedExaminer2.getExams().get(0));
        assertEquals(exam, updatedExaminer3.getExams().get(0));

        assertTrue(exam.getExaminers().contains(updatedExaminer2));
        assertTrue(exam.getExaminers().contains(updatedExaminer3));

        // test with not existing exam
        var oldId = examUpdateDTO.getId();
        examUpdateDTO.setId(-1L);
        mockMvc.perform(put("/exam")
                .contentType(APPLICATION_JSON_UTF8)
                .content(toJson(examUpdateDTO)))
                .andExpect(status().isNotFound());
        examUpdateDTO.setId(oldId);

        // test with not existing member
        oldId = examUpdateDTO.getGraduationMember().get(0).getMemberId();
        examUpdateDTO.getGraduationMember().get(0).setMemberId(-1L);
        mockMvc.perform(put("/exam")
                .contentType(APPLICATION_JSON_UTF8)
                .content(toJson(examUpdateDTO)))
                .andExpect(status().isNotFound());
        examUpdateDTO.getGraduationMember().get(0).setMemberId(oldId);

        // test with not existing graduation
        oldId = examUpdateDTO.getGraduationMember().get(0).getGraduationId();
        examUpdateDTO.getGraduationMember().get(0).setGraduationId(-1L);
        mockMvc.perform(put("/exam")
                .contentType(APPLICATION_JSON_UTF8)
                .content(toJson(examUpdateDTO)))
                .andExpect(status().isNotFound());
        examUpdateDTO.getGraduationMember().get(0).setGraduationId(oldId);

        // test with not existing examiner
        examUpdateDTO.getExaminers().set(0, -1L);
        mockMvc.perform(put("/exam")
                .contentType(APPLICATION_JSON_UTF8)
                .content(toJson(examUpdateDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void exam_can_be_deleted() throws Exception {
        // create member
        var member = TestHelper.createTestMember(contributionClassRepository);

        // create a discipline and a graduation
        var discipline = disciplineRepository.save(Discipline.builder().name("Discipline").build());
        var graduation = graduationRepository.save(TestHelper.createTestGraduation());

        discipline.getGraduations().add(graduation);

        // create an exam and persons
        var exam = examRepository.save(Exam.builder().date(LocalDate.parse("2018-01-02")).build());
        var examiner1 = personRepository.save(Person.builder().name("Test Examiner 1").build());
        var examiner2 = personRepository.save(Person.builder().name("Test Examiner 2").build());

        exam.getExaminers().add(examiner1);
        exam.getExaminers().add(examiner2);
        examiner1.getExams().add(exam);
        examiner2.getExams().add(exam);

        // create a graduation member connection
        var graduationMember = GraduationMember.builder()
                .exam(exam)
                .graduation(graduation)
                .member(member)
                .build();
        exam.getGraduationMember().add(graduationMember);
        graduation.getGraduationMembers().add(graduationMember);
        member.getGraduations().add(graduationMember);

        graduationMemberRepository.save(graduationMember);

        mockMvc.perform(delete("/exam/" + exam.getId()))
                .andExpect(status().isNoContent());

        // fetch the changed entities from the database
        var updatedMember = memberRepository.findById(member.getId()).orElseThrow();
        var updatedExaminer1 = personRepository.findById(examiner1.getId()).orElseThrow();
        var updatedExaminer2 = personRepository.findById(examiner2.getId()).orElseThrow();
        var updatedGraduation = graduationRepository.findById(graduation.getId()).orElseThrow();

        // assert that the exam was deleted successfully
        assertFalse(examRepository.findAll().iterator().hasNext());

        // assert that the exam was removed from the member
        assertFalse(graduationMemberRepository.findAll().iterator().hasNext());
        assertEquals(0, updatedMember.getGraduations().size());

        // assert that the exam was removed from the graduation
        assertEquals(0, updatedGraduation.getGraduationMembers().size());

        // assert that the exam was removed from the persons
        assertEquals(0, updatedExaminer1.getExams().size());
        assertEquals(0, updatedExaminer2.getExams().size());
    }

    @Test
    public void exam_can_be_called_by_id() throws Exception {
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
        exam.getGraduationMember().add(graduationMember);
        graduation.getGraduationMembers().add(graduationMember);
        member.getGraduations().add(graduationMember);

        graduationMember = graduationMemberRepository.save(graduationMember);

        mockMvc.perform(get("/exam/" + exam.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(exam.getId().intValue()))
                .andExpect(jsonPath("$.date").value(is(exam.getDate().toString())))
                .andExpect(jsonPath("$.graduationMember").value(hasSize(1)))
                .andExpect(jsonPath("$.graduationMember[0].id").value(is(graduationMember.getId().intValue())))
                .andExpect(jsonPath("$.examiners").value(hasSize(2)))
                .andExpect(jsonPath("$.examiners[0].id").value(is(examiner1.getId().intValue())))
                .andExpect(jsonPath("$.examiners[0].name").value(is(examiner1.getName())))
                .andExpect(jsonPath("$.examiners[1].id").value(is(examiner2.getId().intValue())))
                .andExpect(jsonPath("$.examiners[1].name").value(is(examiner2.getName())));

        // test with non existing id
        mockMvc.perform(get("/exam/-1")).andExpect(status().isNotFound());
    }


}
