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
}
