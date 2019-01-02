package de.shogundb.domain.exam;

import de.shogundb.domain.graduation.GraduationMember;
import de.shogundb.domain.graduation.GraduationMemberRepository;
import de.shogundb.domain.graduation.GraduationNotFoundException;
import de.shogundb.domain.graduation.GraduationRepository;
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
            for (var graduationMemberDTO : exam.getGraduationMembers()) {
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

        newExam.setGraduationMembers(graduationMembers);

        // update the member
        newExam.getGraduationMembers().forEach(graduationMember -> {
            graduationMember.getMember().getGraduations().add(graduationMember);
            graduationMember.getGraduation().getGraduationMembers().add(graduationMember);
        });

        return saveExam(newExam);
    }

    /**
     * Updates the given exam.
     *
     * @param examUpdateDTO the new values and links of the exam
     * @return a HTTP 201 CREATED if the exam was updated successfully
     * @throws ExamNotFoundException       thrown, if the exam does not exist
     * @throws PersonNotFoundException     thrown, if one of the examiners does not exist
     * @throws MemberNotFoundException     thrown, if one of the members does not exist
     * @throws GraduationNotFoundException thrown, if one of the graduations does not exist
     */
    @PutMapping
    public ResponseEntity<Exam> update(@RequestBody @Valid ExamUpdateDTO examUpdateDTO)
            throws ExamNotFoundException, PersonNotFoundException, MemberNotFoundException, GraduationNotFoundException {
        // fetch the exam from the database
        var existingExam = examRepository.findById(examUpdateDTO.getId())
                .orElseThrow(() -> new ExamNotFoundException(examUpdateDTO.getId()));

        // update the date
        existingExam.setDate(examUpdateDTO.getDate());

        // renew links between the examiner and the exam
        existingExam.getExaminers().forEach(examiner -> examiner.getExams().remove(existingExam));
        existingExam.getExaminers().clear();

        for (var examinerId : examUpdateDTO.getExaminers()) {
            // fetch the person from the database
            var examiner = personRepository.findById(examinerId)
                    .orElseThrow(() -> new PersonNotFoundException(examinerId));

            // add the exam to the examiner
            examiner.getExams().add(existingExam);

            // add the examiner to the exam
            existingExam.getExaminers().add(examiner);
        }

        // renew the graduation member connections
        var graduationMembers = new ArrayList<GraduationMember>() {{
            for (var graduationMember : examUpdateDTO.getGraduationMembers()) {
                // fetch the member
                var member = memberRepository.findById(graduationMember.getMemberId())
                        .orElseThrow(() -> new MemberNotFoundException(graduationMember.getMemberId()));

                // fetch the graduation
                var graduation = graduationRepository.findById(graduationMember.getGraduationId())
                        .orElseThrow(() -> new GraduationNotFoundException(graduationMember.getGraduationId()));

                // create the graduation member connection and add it to the list
                var newGraduationMember = GraduationMember.builder()
                        .member(member)
                        .graduation(graduation)
                        .build();
                member.getGraduations().add(newGraduationMember);
                graduation.getGraduationMembers().add(newGraduationMember);

                add(newGraduationMember);
            }
        }};
        removeGraduationMembers(existingExam);

        // add all new graduation member connections to the exam
        for (var graduationMember : graduationMembers) {
            graduationMember.setExam(existingExam);
            existingExam.getGraduationMembers().add(graduationMember);
        }
        existingExam.setGraduationMembers(graduationMembers);

        return saveExam(existingExam);
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
        var exam = examRepository.findById(id).orElseThrow(() -> new ExamNotFoundException(id));

        // unlink all examiners
        exam.getExaminers().forEach(examiner -> examiner.getExams().remove(exam));
        exam.getExaminers().clear();

        // remove all graduation member links
        removeGraduationMembers(exam);

        examRepository.delete(exam);

        return ResponseEntity.noContent().build();
    }

    /**
     * Shows the exam with the given id.
     *
     * @param id the unique identifier of the exam
     * @return a HTTP 200 OK and the exam, if it exists in the database
     * @throws ExamNotFoundException thrown, if no exam with the given id exists in the database
     */
    @GetMapping("/{id}")
    public ResponseEntity<Exam> show(@PathVariable Long id) throws ExamNotFoundException {
        var exam = examRepository.findById(id).orElseThrow(() -> new ExamNotFoundException(id));

        return ResponseEntity.ok(exam);
    }

    /**
     * Unlinks and removes all graduation members from the given exam.
     *
     * @param exam the exam
     */
    private void removeGraduationMembers(Exam exam) {
        exam.getGraduationMembers().forEach(graduationMember -> {
            // unlink the member
            graduationMember.getMember().getGraduations().remove(graduationMember);

            // unlink the graduation
            graduationMember.getGraduation().getGraduationMembers().remove(graduationMember);

            // remove the graduation member
            graduationMemberRepository.delete(graduationMember);
        });
        exam.getGraduationMembers().clear();
    }

    /**
     * Saves the given exam to the database, creates an uri and returns a HTTP 201 CREATED.
     *
     * @param exam the exam to save
     * @return a HTTP 201 CREATED if the exam was successfully saved to the database
     */
    private ResponseEntity<Exam> saveExam(Exam exam) {
        var updatedExam = examRepository.save(exam);

        URI uri = MvcUriComponentsBuilder.fromController(getClass()).path("/{id}")
                .buildAndExpand(updatedExam.getId()).toUri();

        return ResponseEntity.created(uri).body(updatedExam);
    }
}