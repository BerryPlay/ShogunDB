package de.shogundb.domain.graduation;

import de.shogundb.domain.exam.ExamNotFoundException;
import de.shogundb.domain.exam.ExamRepository;
import de.shogundb.domain.member.MemberNotFoundException;
import de.shogundb.domain.member.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.net.URI;

import static de.shogundb.domain.graduation.GraduationMember.removeGraduationMember;

@RestController
@RequestMapping("/graduationMember")
public class GraduationMemberController {
    private final ExamRepository examRepository;
    private final GraduationMemberRepository graduationMemberRepository;
    private final GraduationRepository graduationRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public GraduationMemberController(
            ExamRepository examRepository,
            GraduationMemberRepository graduationMemberRepository,
            GraduationRepository graduationRepository,
            MemberRepository memberRepository) {
        this.examRepository = examRepository;
        this.graduationMemberRepository = graduationMemberRepository;
        this.graduationRepository = graduationRepository;
        this.memberRepository = memberRepository;
    }

    /**
     * Get a list of all graduation member links from the database.
     *
     * @return a list of all graduation member links
     */
    @GetMapping
    public ResponseEntity<Iterable<GraduationMember>> index() {
        return ResponseEntity.ok(graduationMemberRepository.findAll());
    }

    /**
     * Adds a new  exam to the database and links all given members, persons and graduations to it.
     *
     * @param graduationMember a data transfer object to add a link between
     * @return a HTTP 201 CREATED if the link was set successfully
     * @throws ExamNotFoundException       thrown, if the exam with the given id does not exists
     * @throws MemberNotFoundException     thrown, if the member with the given id does not exists
     * @throws GraduationNotFoundException thrown, if the graduation with the given id does not exists
     */
    @PostMapping
    public ResponseEntity<GraduationMember> store(@RequestBody GraduationMemberRegisterDTO graduationMember)
            throws ExamNotFoundException, MemberNotFoundException, GraduationNotFoundException {
        // check if the exam id is set
        if (graduationMember.getExamId() == null) {
            return ResponseEntity.badRequest().build();
        }

        // fetch the exam, the graduation and the member
        var exam = examRepository.findById(graduationMember.getExamId())
                .orElseThrow(() -> new ExamNotFoundException(graduationMember.getExamId()));

        var graduation = graduationRepository.findById(graduationMember.getGraduationId())
                .orElseThrow(() -> new GraduationNotFoundException(graduationMember.getGraduationId()));

        var member = memberRepository.findById(graduationMember.getMemberId())
                .orElseThrow(() -> new MemberNotFoundException(graduationMember.getMemberId()));

        // link all together
        var newGraduationMember = GraduationMember.builder()
                .exam(exam)
                .graduation(graduation)
                .member(member)
                .build();
        exam.getGraduationMembers().add(newGraduationMember);
        graduation.getGraduationMembers().add(newGraduationMember);
        member.getGraduations().add(newGraduationMember);

        newGraduationMember = graduationMemberRepository.save(newGraduationMember);

        URI uri = MvcUriComponentsBuilder.fromController(getClass()).path("/{id}")
                .buildAndExpand(newGraduationMember.getId()).toUri();

        return ResponseEntity.created(uri).body(newGraduationMember);
    }

    /**
     * Removes the graduation member link with the given id.
     *
     * @param id the unique identifier of the graduation member link
     * @return a HTTP 204 NO CONTENT if the graduation member link was removed successfully
     * @throws GraduationMemberNotFoundException thrown, if the graduation member link with the given id does not exists
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) throws GraduationMemberNotFoundException {
        var graduationMember = graduationMemberRepository.findById(id)
                .orElseThrow(() -> new GraduationMemberNotFoundException(id));

        removeGraduationMember(graduationMember, graduationMemberRepository);

        return ResponseEntity.noContent().build();
    }

    /**
     * Shows the graduation member link with the given id.
     *
     * @param id the unique identifier of the graduation member link
     * @return a HTTP 200 OK and the graduation member link, if it exists in the database
     * @throws GraduationNotFoundException thrown, if the graduation member link with the given id does not exists in
     *                                     the database
     */
    @GetMapping("/{id}")
    public ResponseEntity<GraduationMember> show(@PathVariable Long id) throws GraduationNotFoundException {
        return ResponseEntity.ok(graduationMemberRepository.findById(id)
                .orElseThrow(() -> new GraduationNotFoundException(id)));
    }
}
