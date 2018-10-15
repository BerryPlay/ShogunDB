//package de.shogundb;
//
//import de.shogundb.domain.contributionClass.ContributionClass;
//import de.shogundb.domain.contributionClass.ContributionClassRepository;
//import de.shogundb.domain.discipline.Discipline;
//import de.shogundb.domain.discipline.DisciplineRepository;
//import de.shogundb.domain.event.Event;
//import de.shogundb.domain.event.EventRepository;
//import de.shogundb.domain.graduation.Graduation;
//import de.shogundb.domain.graduation.GraduationRepository;
//import de.shogundb.domain.member.Gender;
//import de.shogundb.domain.member.Member;
//import de.shogundb.domain.member.MemberRepository;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.util.ArrayList;
//import java.util.Date;
//
//@SpringBootTest
//@RunWith(SpringRunner.class)
//public class TestDataGenerator {
//    @Autowired
//    private MemberRepository memberRepository;
//
//    @Autowired
//    private ContributionClassRepository contributionClassRepository;
//
//    @Autowired
//    private DisciplineRepository disciplineRepository;
//
//    @Autowired
//    private GraduationRepository graduationRepository;
//
//    @Autowired
//    private EventRepository eventRepository;
//
//    @Before
//    public void setup() {
//        // contribution class
//        ContributionClass contributionClass = this.contributionClassRepository.save(
//                ContributionClass.builder()
//                        .name("Test Contribution Class")
//                        .baseContribution(27.5)
//                        .additionalContribution(5.0)
//                        .build());
//
//        // events
//        ArrayList<Event> events = new ArrayList<>();
//
//        Event firstEvent = Event.builder()
//                .name("Test Event")
//                .date(new Date(1514764800000L))
//                .build();
//
//        // this.entityManager.persist(firstEvent);
//        events.add(this.eventRepository.save(firstEvent));
//
//        Event secondEvent = Event.builder()
//                .name("Another Test Event")
//                .date(new Date(1514764880000L))
//                .build();
//
//        // this.entityManager.persist(secondEvent);
//        events.add(this.eventRepository.save(secondEvent));
//
//        // disciplines
//        Discipline discipline1 = Discipline.builder()
//                .name("Test Discipline")
//                .build();
//
//        Discipline discipline2 = Discipline.builder()
//                .name("Another Test Discipline")
//                .build();
//
//        discipline1 = this.disciplineRepository.save(discipline1);
//        discipline2 = this.disciplineRepository.save(discipline2);
//
//        // graduations
//        Graduation firstGraduation = Graduation.builder()
//                .name("Test Graduation 1")
//                .color("Brown")
//                .build();
//
//        firstGraduation.setDiscipline(discipline1);
//        discipline1.getGraduations().add(firstGraduation);
//
//        Graduation secondGraduation = Graduation.builder()
//                .name("Another Test Graduation 1")
//                .color("Blue")
//                .build();
//
//        secondGraduation.setDiscipline(discipline1);
//        discipline1.getGraduations().add(secondGraduation);
//
//        Graduation thirdGraduation = Graduation.builder()
//                .name("Test Graduation 2")
//                .color("Brown")
//                .build();
//
//        thirdGraduation.setDiscipline(discipline2);
//        discipline2.getGraduations().add(thirdGraduation);
//
//        Graduation fourthGraduation = Graduation.builder()
//                .name("Another Test Graduation 2")
//                .color("Blue")
//                .build();
//        fourthGraduation.setDiscipline(discipline2);
//        discipline2.getGraduations().add(fourthGraduation);
//
//        discipline1 = this.disciplineRepository.save(discipline1);
//        discipline2 = this.disciplineRepository.save(discipline2);
//
//        // member
//        Member member = Member.builder()
//                .forename("Max")
//                .surname("Mustermann")
//                .gender(Gender.MALE)
//                .street("Musterstraße")
//                .postcode("26721")
//                .phoneNumber("04929 5435438")
//                .mobileNumber("1522 416845575")
//                .email("max@muster.de")
//                .dateOfBirth(new Date(810086400000L))
//                .hasBudoPass(true)
//                .budoPassDate(new Date(1514764800000L))
//                .enteredDate(new Date(1514764800000L))
//                .hasLeft(false)
//                .leftDate(null)
//                .isPassive(false)
//                .accountHolder("Max Mustermann")
//                .contributionClass(contributionClass)
//                .build();
//        member = this.memberRepository.save(member);
//
//        discipline1.getMembers().add(member);
//        discipline2.getMembers().add(member);
//
//        events.get(0).getMembers().add(member);
//        events.get(1).getMembers().add(member);
//
//        member.getEvents().addAll(events);
//
//        ArrayList<Discipline> disciplines = new ArrayList<>();
//        disciplines.add(discipline1);
//        disciplines.add(discipline2);
//
//        member.getDisciplines().addAll(disciplines);
//
//        this.memberRepository.save(member);
//    }
//
//    @Test
//    public void setup_complete() {
//        Assert.assertTrue(true);
//    }
//}
