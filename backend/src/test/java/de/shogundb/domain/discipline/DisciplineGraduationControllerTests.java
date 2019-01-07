package de.shogundb.domain.discipline;

import de.shogundb.TestHelper;
import de.shogundb.domain.contributionClass.ContributionClassRepository;
import de.shogundb.domain.exam.Exam;
import de.shogundb.domain.exam.ExamRepository;
import de.shogundb.domain.graduation.Graduation;
import de.shogundb.domain.graduation.GraduationMember;
import de.shogundb.domain.graduation.GraduationMemberRepository;
import de.shogundb.domain.graduation.GraduationRepository;
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
import java.util.List;

import static de.shogundb.TestHelper.toJson;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles(profiles = "test")
@AutoConfigureTestDatabase
@Transactional
public class DisciplineGraduationControllerTests {

    @Autowired
    private ContributionClassRepository contributionClassRepository;

    @Autowired
    private DisciplineRepository disciplineRepository;

    @Autowired
    private GraduationMemberRepository graduationMemberRepository;

    @Autowired
    private GraduationRepository graduationRepository;

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void all_graduations_of_discipline_can_be_called() throws Exception {
        Discipline discipline = disciplineRepository.save(Discipline.builder()
                .name("Test Discipline")
                .build());

        Graduation graduation1 = graduationRepository.save(Graduation.builder()
                .name("First Test Graduation")
                .color("Blue")
                .examConditions("Test Exam Conditions")
                .highlightConditions("Test Highlight Conditions")
                .build());

        Graduation graduation2 = graduationRepository.save(Graduation.builder()
                .name("Second Test Graduation")
                .color("Black")
                .examConditions("Another Test Exam Conditions")
                .highlightConditions("Another Test Highlight Conditions")
                .build());

        discipline.getGraduations().add(graduation1);
        discipline.getGraduations().add(graduation2);

        graduation1.setDiscipline(discipline);
        graduation2.setDiscipline(discipline);

        discipline = disciplineRepository.save(discipline);

        mockMvc.perform(get("/discipline/graduation/" + discipline.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(is(graduation1.getId().intValue())))
                .andExpect(jsonPath("$[0].name").value(is(graduation1.getName())))
                .andExpect(jsonPath("$[0].color").value(is(graduation1.getColor())))
                .andExpect(jsonPath("$[0].examConditions").value(is(graduation1.getExamConditions())))
                .andExpect(jsonPath("$[0].highlightConditions").value(is(graduation1.getHighlightConditions())))
                .andExpect(jsonPath("$[1].id").value(is(graduation2.getId().intValue())))
                .andExpect(jsonPath("$[1].name").value(is(graduation2.getName())))
                .andExpect(jsonPath("$[1].color").value(is(graduation2.getColor())))
                .andExpect(jsonPath("$[1].examConditions").value(is(graduation2.getExamConditions())))
                .andExpect(jsonPath("$[1].highlightConditions").value(is(graduation2.getHighlightConditions())));

        mockMvc.perform(get("/discipline/graduation/-1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void graduation_can_be_added_to_discipline() throws Exception {
        Discipline discipline = disciplineRepository.save(Discipline.builder()
                .name("Test Discipline")
                .build());

        Graduation graduation = Graduation.builder()
                .name("First Test Graduation")
                .color("Blue")
                .examConditions("Test Exam Conditions")
                .highlightConditions("Test Highlight Conditions")
                .build();

        mockMvc.perform(post("/discipline/graduation/" + discipline.getId())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(toJson(graduation)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").value(hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(notNullValue()))
                .andExpect(jsonPath("$[0].name").value(is(graduation.getName())))
                .andExpect(jsonPath("$[0].color").value(is(graduation.getColor())))
                .andExpect(jsonPath("$[0].examConditions").value(is(graduation.getExamConditions())))
                .andExpect(jsonPath("$[0].highlightConditions").value(is(graduation.getHighlightConditions())));

        List<Graduation> graduations = disciplineRepository.findById(discipline.getId()).map(Discipline::getGraduations)
                .orElseThrow(DisciplineNotFoundException::new);

        assertEquals(graduations.size(), 1);
        assertEquals(graduations.get(0).getName(), graduation.getName());
        assertEquals(graduations.get(0).getColor(), graduation.getColor());
        assertEquals(graduations.get(0).getExamConditions(), graduation.getExamConditions());
        assertEquals(graduations.get(0).getHighlightConditions(), graduation.getHighlightConditions());
    }

    @Test
    public void graduation_can_be_removed_from_discipline() throws Exception {
        // create test member
        var member = memberRepository.save(TestHelper.createTestMember(contributionClassRepository));

        Discipline discipline = disciplineRepository.save(Discipline.builder()
                .name("Test Discipline")
                .build());

        Graduation graduation = Graduation.builder()
                .name("First Test Graduation")
                .color("Blue")
                .examConditions("Test Exam Conditions")
                .highlightConditions("Test Highlight Conditions")
                .discipline(discipline)
                .build();

        discipline.getGraduations().add(graduation);

        graduation = graduationRepository.save(graduation);

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

        mockMvc.perform(delete("/discipline/graduation/" + graduation.getId()))
                .andExpect(status().isNoContent());

        // update the entities
        var updatedMember = memberRepository.findById(member.getId()).orElseThrow();
        var updatedExam = examRepository.findById(exam.getId()).orElseThrow();

        // check if the graduation was removed successfully
        assertFalse(graduationRepository.existsById(graduation.getId()));

        // check if the graduation member link was removed successfully
        assertFalse(graduationMemberRepository.existsById(graduationMember.getId()));

        // check if the links are removed from the other entities
        assertEquals(0, updatedMember.getGraduations().size());
        assertEquals(0, updatedExam.getGraduationMembers().size());
    }
}
