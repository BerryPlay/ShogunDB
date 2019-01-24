package de.shogundb;

import de.shogundb.conditions.DatabaseType;
import de.shogundb.conditions.MainCondition;
import de.shogundb.conditions.statements.AgeCondition;
import de.shogundb.conditions.statements.AndCondition;
import de.shogundb.conditions.statements.MembershipCondition;
import de.shogundb.domain.championship.ChampionshipRepository;
import de.shogundb.domain.contributionClass.ContributionClass;
import de.shogundb.domain.contributionClass.ContributionClassRepository;
import de.shogundb.domain.discipline.Discipline;
import de.shogundb.domain.discipline.DisciplineRepository;
import de.shogundb.domain.event.EventRepository;
import de.shogundb.domain.exam.Exam;
import de.shogundb.domain.exam.ExamRepository;
import de.shogundb.domain.graduation.Graduation;
import de.shogundb.domain.graduation.GraduationMember;
import de.shogundb.domain.graduation.GraduationMemberRepository;
import de.shogundb.domain.graduation.GraduationRepository;
import de.shogundb.domain.member.Gender;
import de.shogundb.domain.member.Member;
import de.shogundb.domain.member.MemberRepository;
import de.shogundb.domain.person.Person;
import de.shogundb.domain.person.PersonRepository;
import de.shogundb.domain.seminar.SeminarRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.ArrayList;

import static de.shogundb.conditions.PeriodFormat.MONTH;
import static org.junit.Assert.assertEquals;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles(profiles = "test")
@AutoConfigureTestDatabase
@Transactional
public class ConditionTests {
    // test objects
    public ContributionClass contributionClass;
    public Member member1;
    public Member member2;
    public Discipline discipline1;
    public Discipline discipline2;
    public Graduation graduation1;
    public Graduation graduation2;
    public Person person1;
    public Person person2;
    public Exam exam;
    public GraduationMember graduationMember1;
    public GraduationMember graduationMember2;
    @Autowired
    private ChampionshipRepository championshipRepository;
    @Autowired
    private ContributionClassRepository contributionClassRepository;
    @Autowired
    private DisciplineRepository disciplineRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private ExamRepository examRepository;
    @Autowired
    private GraduationMemberRepository graduationMemberRepository;
    @Autowired
    private GraduationRepository graduationRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private SeminarRepository seminarRepository;
    @PersistenceContext
    private EntityManager enitityManager;
    @Value("${spring.jpa.database}")
    private String databaseType;

    public void setupEnvironment() {
        contributionClass = null;
        member1 = null;
        member2 = null;
        discipline1 = null;
        discipline2 = null;
        graduation1 = null;
        graduation2 = null;
        person1 = null;
        person2 = null;
        exam = null;
        graduationMember1 = null;
        graduationMember2 = null;

        contributionClass = contributionClassRepository
                .save(ContributionClass.builder()
                        .name("Test")
                        .baseContribution(27.7)
                        .additionalContribution(5)
                        .build());

        /*
         * Member 1:
         *     Age: 10
         *     Member: since 7 month
         */
        member1 = memberRepository.save(Member.builder()
                .forename("Max")
                .surname("Mustermann")
                .gender(Gender.MALE)
                .street("Musterstraße")
                .postcode("26721")
                .city("Emden")
                .phoneNumber("04929 5435438")
                .mobileNumber("1522 416845575")
                .email("max@muster.de")
                .dateOfBirth(LocalDate.now().minusYears(10).minusDays(30))
                .hasBudoPass(true)
                .budoPassDate(LocalDate.now())
                .enteredDate(LocalDate.now().minusMonths(7))
                .hasLeft(true)
                .isPassive(false)
                .contributionClass(contributionClass)
                .accountHolder("Max Mustermann")
                .build());

        /*
         * Member :
         *     Age: 21
         *     Member: since 2 years
         */
        member2 = memberRepository.save(Member.builder()
                .forename("Maxima")
                .surname("Musterfrau")
                .gender(Gender.FEMALE)
                .street("Musterstraße")
                .postcode("26721")
                .city("Emden")
                .phoneNumber("04929 5435438")
                .mobileNumber("1522 416845575")
                .email("max@muster.de")
                .dateOfBirth(LocalDate.now().minusYears(21).minusDays(50))
                .hasBudoPass(true)
                .budoPassDate(LocalDate.now())
                .enteredDate(LocalDate.now().minusYears(2))
                .hasLeft(true)
                .isPassive(false)
                .contributionClass(contributionClass)
                .accountHolder("Max Mustermann")
                .build());

        // create disciplines
        discipline1 = disciplineRepository.save(Discipline.builder().name("Jiu-Jitsu").build());

        // create graduations
        graduation1 = graduationRepository.save(Graduation.builder()
                .name("1. Kyu")
                .color("yellow")
                .build());

        graduation2 = graduationRepository.save(Graduation.builder()
                .name("1. Kyu")
                .color("orange")
                .build());

        // link the graduations and the discipline
        discipline1.getGraduations().add(graduation1);
        discipline1.getGraduations().add(graduation2);
        graduation1.setDiscipline(discipline1);
        graduation2.setDiscipline(discipline1);
        discipline1 = disciplineRepository.save(discipline1);

        // create persons
        person1 = personRepository.save(Person.builder().name("Examiner 1").build());
        person2 = personRepository.save(Person.builder().name("Examiner 2").build());

        // create an exam
        exam = examRepository.save(Exam.builder()
                .date(LocalDate.now().minusMonths(4))
                .examiners(new ArrayList<>() {{
                    add(person1);
                    add(person2);
                }})
                .build());

        // create graduation member link
        graduationMember1 = graduationMemberRepository.save(GraduationMember.builder()
                .graduation(graduation1)
                .exam(exam)
                .member(member1)
                .build());

        graduationMember2 = graduationMemberRepository.save(GraduationMember.builder()
                .graduation(graduation2)
                .exam(exam)
                .member(member2)
                .build());

        // link the exam and graduation member links
        exam.getGraduationMembers().add(graduationMember1);
        exam.getGraduationMembers().add(graduationMember2);
        exam = examRepository.save(exam);
    }

    @Test
    public void and_condition_works_properly() {
        setupEnvironment();

        // create a simple condition
        var mainCondition = MainCondition.builder()
                .condition(AndCondition.builder().conditions(new ArrayList<>() {{
                    add(new AgeCondition(15, Integer.MAX_VALUE));
                    add(new MembershipCondition(9, MONTH));
                }}).build()).build();

        var members = mainCondition.getMembers(enitityManager, DatabaseType.valueOf(databaseType));

        assertEquals(1, members.size());
        assertEquals(member2, members.get(0));
    }

    @Test
    public void age_condition_works_properly() {
        setupEnvironment();

        // should return only member 2
        var condition1 = new MainCondition(new AgeCondition(21, 25));
        var members1 = condition1.getMembers(enitityManager, DatabaseType.valueOf(databaseType));
        assertEquals(1, members1.size());
        assertEquals(member2, members1.get(0));

        // should return no members
        System.out.println(member2.getDateOfBirth());
        var condition2 = new MainCondition(new AgeCondition(19, 20));
        System.out.println(condition2.getSQLQuery(DatabaseType.valueOf(databaseType)));
        var members2 = condition2.getMembers(enitityManager, DatabaseType.valueOf(databaseType));
        assertEquals(0, members2.size());
    }
}
