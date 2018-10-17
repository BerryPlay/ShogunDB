package de.shogundb.domain.discipline;

import de.shogundb.domain.graduation.Graduation;
import de.shogundb.domain.graduation.GraduationRepository;
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

import java.util.List;

import static de.shogundb.TestHelper.toJson;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
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
public class DisciplineGraduationControllerTests {

    @Autowired
    private DisciplineRepository disciplineRepository;

    @Autowired
    private GraduationRepository graduationRepository;

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
                .andExpect(status().isConflict());
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
                .name("Another Test Graduation")
                .color("Black")
                .examConditions("Another Test Exam Conditions")
                .highlightConditions("Another Test Highlight Conditions")
                .build());

        discipline.getGraduations().add(graduation1);
        discipline.getGraduations().add(graduation2);

        graduation1.setDiscipline(discipline);
        graduation2.setDiscipline(discipline);

        discipline = disciplineRepository.save(discipline);

        mockMvc.perform(delete("/discipline/graduation/" + graduation1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(notNullValue()))
                .andExpect(jsonPath("$[0].name").value(is(graduation2.getName())))
                .andExpect(jsonPath("$[0].color").value(is(graduation2.getColor())))
                .andExpect(jsonPath("$[0].examConditions").value(is(graduation2.getExamConditions())))
                .andExpect(jsonPath("$[0].highlightConditions").value(is(graduation2.getHighlightConditions())));

        List<Graduation> graduations = disciplineRepository.findById(discipline.getId()).map(Discipline::getGraduations)
                .orElseThrow(DisciplineNotFoundException::new);

        assertEquals(graduations.size(), 1);
        assertEquals(graduations.get(0).getName(), graduation2.getName());
        assertEquals(graduations.get(0).getColor(), graduation2.getColor());
        assertEquals(graduations.get(0).getExamConditions(), graduation2.getExamConditions());
        assertEquals(graduations.get(0).getHighlightConditions(), graduation2.getHighlightConditions());
    }
}
