package de.shogundb.domain.graduation;

import de.shogundb.domain.member.MemberNotFoundException;
import de.shogundb.domain.member.MemberRepository;
import de.shogundb.domain.person.Person;
import de.shogundb.domain.person.PersonNotFoundException;
import de.shogundb.domain.person.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;

@RestController
@RequestMapping("/exam")
public class ExamController {
    private final ExamRepository examRepository;
    private final GraduationRepository graduationRepository;
    private final GraduationMemberRepository graduationMemberRepository;
    private final MemberRepository memberRepository;
    private final PersonRepository personRepository;

    @Autowired
    public ExamController(
            ExamRepository examRepository,
            GraduationMemberRepository graduationMemberRepository,
            GraduationRepository graduationRepository,
            MemberRepository memberRepository,
            PersonRepository personRepository) {
        this.examRepository = examRepository;
        this.graduationRepository = graduationRepository;
        this.graduationMemberRepository = graduationMemberRepository;
        this.memberRepository = memberRepository;
        this.personRepository = personRepository;
    }

    /**
     * Get a list of all exams from the database.
     *
     * @return a list of all exams
     */
    @GetMapping
    public ResponseEntity<Iterable<Exam>> index() {
        return ResponseEntity.ok(examRepository.findAll());
    }

    /**
     * Adds a new  exam to the database and links all given members, persons and graduations to it.
     *
     * @param exam a data transfer object with all needed information
     * @return a HTTP 201 CREATED if the exam was added successfully
     * @throws PersonNotFoundException     thrown, if any of the given persons are not in the database
     * @throws MemberNotFoundException     thrown, if any of the given members are not in the database
     * @throws GraduationNotFoundException thrown, if any of the given graduations are not in the database
     */
    @PostMapping
    public ResponseEntity<Exam> store(@RequestBody @Valid ExamRegisterDTO exam)
            throws PersonNotFoundException, MemberNotFoundException, GraduationNotFoundException {
        // get all persons
        var examiners = new ArrayList<Person>() {{
            for (Long personId : exam.getExaminers()) {
                add(personRepository.findById(personId).orElseThrow(() -> new PersonNotFoundException(personId)));
            }
        }};

        var newExam = Exam.builder()
                .date(exam.getDate())
                .examiners(examiners)
                .build();

        // create all graduation member exam links
        var graduationMembers = new ArrayList<GraduationMember>() {{
            for (var graduationMemberDTO : exam.getGraduationMember()) {
                add(GraduationMember.builder()
                        .member(memberRepository
                                .findById(graduationMemberDTO.getMemberId())
                                .orElseThrow(() -> new MemberNotFoundException(graduationMemberDTO.getMemberId())))
                        .graduation(graduationRepository
                                .findById(graduationMemberDTO.getGraduationId())
                                .orElseThrow(()
                                        -> new GraduationNotFoundException(graduationMemberDTO.getGraduationId())))
                        .exam(newExam)
                        .build());
            }
        }};

        newExam.setGraduationMember(graduationMembers);

        // update the member
        newExam.getGraduationMember().forEach(graduationMember -> {
            graduationMember.getMember().getGraduations().add(graduationMember);
            graduationMember.getGraduation().getGraduationMembers().add(graduationMember);
        });

        // save the exam
        var savedExam = examRepository.save(newExam);

        URI uri = MvcUriComponentsBuilder.fromController(getClass()).path("/{id}")
                .buildAndExpand(savedExam.getId()).toUri();

        return ResponseEntity.created(uri).body(savedExam);
    }

    /**
     * Removes the exam with the given id from the database.
     *
     * @param id the unique identifier of the exam
     * @return a HTTP 204 NO CONTENT if the exam was removed successfully
     * @throws ExamNotFoundException thrown, if an exam with the given id does not exist
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) throws ExamNotFoundException {
        Exam exam = examRepository.findById(id).orElseThrow(() -> new ExamNotFoundException(id));

        // unlink all examiners
        exam.getExaminers().forEach(examiner -> examiner.getExams().remove(exam));
        exam.getExaminers().clear();

        // remove all graduation member links
        exam.getGraduationMember().forEach(graduationMember -> {
            // unlink the member
            graduationMember.getMember().getGraduations().remove(graduationMember);

            // unlink the graduation
            graduationMember.getGraduation().getGraduationMembers().remove(graduationMember);

            // remove the graduation member
            graduationMemberRepository.delete(graduationMember);
        });
        exam.getGraduationMember().clear();

        examRepository.delete(exam);

        return ResponseEntity.noContent().build();
    }
}