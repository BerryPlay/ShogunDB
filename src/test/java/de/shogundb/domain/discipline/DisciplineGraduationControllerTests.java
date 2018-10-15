package de.shogundb.domain.discipline;

import com.google.gson.Gson;
import de.shogundb.domain.contributionClass.ContributionClassRepository;
import de.shogundb.domain.graduation.Graduation;
import de.shogundb.domain.graduation.GraduationRepository;
import de.shogundb.domain.member.MemberRepository;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertEquals;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles(profiles = "test")
@AutoConfigureTestDatabase
@Transactional
public class DisciplineGraduationControllerTests {

    @Autowired
    private DisciplineRepository disciplineRepository;

    @Autowired
    private GraduationRepository graduationRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ContributionClassRepository contributionClassRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    public void all_graduations_of_discipline_can_be_called() throws Exception {
        Discipline discipline = this.disciplineRepository.save(Discipline.builder()
                .name("Test Discipline")
                .build());

        Graduation graduation1 = this.graduationRepository.save(Graduation.builder()
                .name("First Test Graduation")
                .color("Blue")
                .examConditions("Test Exam Conditions")
                .highlightConditions("Test Highlight Conditions")
                .build());

        Graduation graduation2 = this.graduationRepository.save(Graduation.builder()
                .name("Second Test Graduation")
                .color("Black")
                .examConditions("Another Test Exam Conditions")
                .highlightConditions("Another Test Highlight Conditions")
                .build());

        discipline.getGraduations().add(graduation1);
        discipline.getGraduations().add(graduation2);

        graduation1.setDiscipline(discipline);
        graduation2.setDiscipline(discipline);

        discipline = this.disciplineRepository.save(discipline);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/discipline/graduation/" + discipline.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", is(graduation1.getId().intValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", is(graduation1.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].color", is(graduation1.getColor())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].examConditions", is(graduation1.getExamConditions())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].highlightConditions", is(graduation1.getHighlightConditions())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id", is(graduation2.getId().intValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name", is(graduation2.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].color", is(graduation2.getColor())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].examConditions", is(graduation2.getExamConditions())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].highlightConditions", is(graduation2.getHighlightConditions())));

        this.mockMvc.perform(MockMvcRequestBuilders.get("/discipline/graduation/-1"))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    public void graduation_can_be_added_to_discipline() throws Exception {
        Discipline discipline = this.disciplineRepository.save(Discipline.builder()
                .name("Test Discipline")
                .build());

        Graduation graduation = Graduation.builder()
                .name("First Test Graduation")
                .color("Blue")
                .examConditions("Test Exam Conditions")
                .highlightConditions("Test Highlight Conditions")
                .build();

        this.mockMvc.perform(MockMvcRequestBuilders.post("/discipline/graduation/" + discipline.getId())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(new Gson().toJson(graduation)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", is(graduation.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].color", is(graduation.getColor())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].examConditions", is(graduation.getExamConditions())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].highlightConditions", is(graduation.getHighlightConditions())));

        List<Graduation> graduations = this.disciplineRepository.findById(discipline.getId()).map(Discipline::getGraduations)
                .orElseThrow(DisciplineNotFoundException::new);

        assertEquals(graduations.size(), 1);
        assertEquals(graduations.get(0).getName(), graduation.getName());
        assertEquals(graduations.get(0).getColor(), graduation.getColor());
        assertEquals(graduations.get(0).getExamConditions(), graduation.getExamConditions());
        assertEquals(graduations.get(0).getHighlightConditions(), graduation.getHighlightConditions());
    }

    @Test
    public void graduation_can_be_removed_from_discipline() throws Exception {
        Discipline discipline = this.disciplineRepository.save(Discipline.builder()
                .name("Test Discipline")
                .build());

        Graduation graduation1 = this.graduationRepository.save(Graduation.builder()
                .name("First Test Graduation")
                .color("Blue")
                .examConditions("Test Exam Conditions")
                .highlightConditions("Test Highlight Conditions")
                .build());

        Graduation graduation2 = this.graduationRepository.save(Graduation.builder()
                .name("Another Test Graduation")
                .color("Black")
                .examConditions("Another Test Exam Conditions")
                .highlightConditions("Another Test Highlight Conditions")
                .build());

        discipline.getGraduations().add(graduation1);
        discipline.getGraduations().add(graduation2);

        graduation1.setDiscipline(discipline);
        graduation2.setDiscipline(discipline);

        discipline = this.disciplineRepository.save(discipline);

        this.mockMvc.perform(MockMvcRequestBuilders.delete("/discipline/graduation/" + graduation1.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", is(graduation2.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].color", is(graduation2.getColor())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].examConditions", is(graduation2.getExamConditions())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].highlightConditions", is(graduation2.getHighlightConditions())));

        List<Graduation> graduations = this.disciplineRepository.findById(discipline.getId()).map(Discipline::getGraduations)
                .orElseThrow(DisciplineNotFoundException::new);

        assertEquals(graduations.size(), 1);
        assertEquals(graduations.get(0).getName(), graduation2.getName());
        assertEquals(graduations.get(0).getColor(), graduation2.getColor());
        assertEquals(graduations.get(0).getExamConditions(), graduation2.getExamConditions());
        assertEquals(graduations.get(0).getHighlightConditions(), graduation2.getHighlightConditions());
    }
}
