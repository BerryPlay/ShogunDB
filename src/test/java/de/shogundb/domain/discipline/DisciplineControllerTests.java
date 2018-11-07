package de.shogundb.domain.discipline;

import de.shogundb.domain.contributionClass.ContributionClassRepository;
import de.shogundb.domain.graduation.Graduation;
import de.shogundb.domain.graduation.GraduationRepository;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static de.shogundb.TestHelper.createTestMember;
import static de.shogundb.TestHelper.toJson;
import static junit.framework.TestCase.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles(profiles = "test")
@AutoConfigureTestDatabase
@Transactional
public class DisciplineControllerTests {
    @Autowired
    private ContributionClassRepository contributionClassRepository;

    @Autowired
    private DisciplineRepository disciplineRepository;

    @Autowired
    private GraduationRepository graduationRepository;

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
    public void all_disciplines_can_be_called() throws Exception {
        Discipline discipline = disciplineRepository.save(Discipline.builder()
                .name("Test Discipline")
                .build());

        mockMvc.perform(get("/discipline"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(is(discipline.getId().intValue())))
                .andExpect(jsonPath("$[0].name").value(is(discipline.getName())));
    }

    @Test
    public void discipline_can_be_added() throws Exception {
        Discipline discipline = Discipline.builder()
                .name("Test Discipline")
                .build();

        mockMvc.perform(post("/discipline")
                .contentType(APPLICATION_JSON_UTF8)
                .content(toJson(discipline)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(notNullValue()))
                .andExpect(jsonPath("$.name").value(is(discipline.getName())));

        disciplineRepository.findAll().forEach(existing -> assertEquals(existing.getName(), discipline.getName()));
    }

    @Test
    public void discipline_can_be_updated() throws Exception {
        Discipline discipline = disciplineRepository.save(Discipline.builder()
                .name("Test Discipline")
                .build());

        DisciplineUpdateDTO disciplineUpdateDTO = DisciplineUpdateDTO.builder()
                .id(discipline.getId())
                .name("New Name")
                .build();

        mockMvc.perform(put("/discipline")
                .contentType(APPLICATION_JSON_UTF8)
                .content(toJson(disciplineUpdateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(is(discipline.getId().intValue())))
                .andExpect(jsonPath("$.name").value(is(disciplineUpdateDTO.getName())));

        discipline = disciplineRepository.findById(discipline.getId()).orElseThrow(DisciplineNotFoundException::new);

        assertEquals(disciplineUpdateDTO.getName(), discipline.getName());
    }

    @Test
    public void discipline_can_be_deleted() throws Exception {
        Discipline discipline = disciplineRepository.save(Discipline.builder()
                .name("Test Discipline")
                .build());

        Graduation graduation = graduationRepository.save(Graduation.builder()
                .name("Test Graduation")
                .color("blue")
                .discipline(discipline)
                .build());

        Member member = memberRepository.save(createTestMember(contributionClassRepository));

        member.getDisciplines().add(discipline);
        discipline.getMembers().add(member);
        discipline.getGraduations().add(graduation);
        discipline = disciplineRepository.save(discipline);

        mockMvc.perform(delete("/discipline/" + discipline.getId()))
                .andExpect(status().isNoContent());

        member = memberRepository.findById(member.getId()).orElseThrow(MemberNotFoundException::new);

        assertFalse(disciplineRepository.findById(discipline.getId()).isPresent());
        assertFalse(graduationRepository.findById(graduation.getId()).isPresent());
        assertTrue(memberRepository.findById(member.getId()).isPresent());
        assertEquals(0, member.getDisciplines().size());
    }

    @Test
    public void discipline_can_be_found_by_id() throws Exception {
        Discipline discipline = disciplineRepository.save(Discipline.builder()
                .name("Test Discipline")
                .build());

        mockMvc.perform(get("/discipline/" + discipline.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(is(discipline.getId().intValue())))
                .andExpect(jsonPath("$.name").value(is(discipline.getName())));

        mockMvc.perform(get("/discipline/-1"))
                .andExpect(status().isConflict());
    }
}
