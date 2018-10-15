package de.shogundb.domain.discipline;

import com.google.gson.Gson;
import de.shogundb.domain.graduation.Graduation;
import de.shogundb.domain.graduation.GraduationRepository;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;

import static junit.framework.TestCase.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles(profiles = "test")
@AutoConfigureTestDatabase
@Transactional
public class DisciplineControllerTests {

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
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    public void all_disciplines_can_be_called() throws Exception {
        Discipline discipline = this.disciplineRepository.save(Discipline.builder()
                .name("Test Discipline")
                .build());

        this.mockMvc.perform(MockMvcRequestBuilders.get("/discipline"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", is(discipline.getId().intValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", is(discipline.getName())));
    }

    @Test
    public void discipline_can_be_added() throws Exception {
        Discipline discipline = Discipline.builder()
                .name("Test Discipline")
                .build();

        this.mockMvc.perform(MockMvcRequestBuilders.post("/discipline")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(new Gson().toJson(discipline)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", is(discipline.getName())));

        this.disciplineRepository.findAll().forEach(existing -> assertEquals(existing.getName(), discipline.getName()));
    }

    @Test
    public void discipline_can_be_updated() throws Exception {
        Discipline discipline = this.disciplineRepository.save(Discipline.builder()
                .name("Test Discipline")
                .build());

        DisciplineUpdateDTO disciplineUpdateDTO = DisciplineUpdateDTO.builder()
                .id(discipline.getId())
                .name("New Name")
                .build();

        this.mockMvc.perform(MockMvcRequestBuilders.post("/discipline")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(new Gson().toJson(disciplineUpdateDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", is(discipline.getId().intValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", is(disciplineUpdateDTO.getName())));

        discipline = this.disciplineRepository.findById(discipline.getId()).orElseThrow(DisciplineNotFoundException::new);

        assertEquals(disciplineUpdateDTO.getName(), discipline.getName());
    }

    @Test
    public void discipline_can_be_deleted() throws Exception {
        Discipline discipline = this.disciplineRepository.save(Discipline.builder()
                .name("Test Discipline")
                .build());

        Graduation graduation = this.graduationRepository.save(Graduation.builder()
                .name("Test Graduation")
                .color("blue")
                .discipline(discipline)
                .build());

        Member member = this.memberRepository.save(this.createTestMember());

        member.getDisciplines().add(discipline);
        discipline.getMembers().add(member);
        discipline.getGraduations().add(graduation);
        discipline = this.disciplineRepository.save(discipline);

        this.mockMvc.perform(MockMvcRequestBuilders.delete("/discipline/" + discipline.getId()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        member = this.memberRepository.findById(member.getId()).orElseThrow(MemberNotFoundException::new);

        assertFalse(this.disciplineRepository.findById(discipline.getId()).isPresent());
        assertFalse(this.graduationRepository.findById(graduation.getId()).isPresent());
        assertTrue(this.memberRepository.findById(member.getId()).isPresent());
        assertEquals(0, member.getDisciplines().size());
    }

    @Test
    public void discipline_can_be_found_by_id() throws Exception {
        Discipline discipline = this.disciplineRepository.save(Discipline.builder()
                .name("Test Discipline")
                .build());

        this.mockMvc.perform(MockMvcRequestBuilders.get("/discipline/" + discipline.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", is(discipline.getId().intValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", is(discipline.getName())));

        this.mockMvc.perform(MockMvcRequestBuilders.get("/discipline/-1"))
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
                .accountHolder("Max Mustermann")
                .build();
    }
}
