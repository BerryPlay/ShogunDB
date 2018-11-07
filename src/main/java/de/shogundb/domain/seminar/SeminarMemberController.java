package de.shogundb.domain.seminar;

import de.shogundb.domain.member.Member;
import de.shogundb.domain.member.MemberNotFoundException;
import de.shogundb.domain.member.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/seminar/member")
public class SeminarMemberController {
    private final SeminarRepository seminarRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public SeminarMemberController(SeminarRepository seminarRepository, MemberRepository memberRepository) {
        this.seminarRepository = seminarRepository;
        this.memberRepository = memberRepository;
    }

    @GetMapping("/{seminarId}")
    public ResponseEntity<Iterable<Member>> index(@PathVariable Long seminarId) throws SeminarNotFoundException {
        Seminar seminar = seminarRepository.findById(seminarId)
                .orElseThrow(() -> new SeminarNotFoundException(seminarId));

        return ResponseEntity.ok(seminar.getMembers());
    }

    @PostMapping("/{seminarId}/{memberId}")
    public ResponseEntity<?> store(@PathVariable Long seminarId, @PathVariable Long memberId)
            throws SeminarNotFoundException, MemberNotFoundException {
        Seminar seminar = seminarRepository.findById(seminarId)
                .orElseThrow(() -> new SeminarNotFoundException(seminarId));

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberNotFoundException(memberId));

        // check if the member is already linked to the seminar
        if (seminar.getMembers().contains(member) || member.getSeminars().contains(seminar)) {
            return ResponseEntity.status(409).build();
        }

        seminar.getMembers().add(member);
        member.getSeminars().add(seminar);

        seminar = seminarRepository.save(seminar);

        URI uri = MvcUriComponentsBuilder.fromController(getClass()).buildAndExpand(seminar.getId()).toUri();

        return ResponseEntity.created(uri).build();
    }

    @DeleteMapping("/{seminarId}/{memberId}")
    public ResponseEntity<?> delete(@PathVariable Long seminarId, @PathVariable Long memberId)
            throws SeminarNotFoundException, MemberNotFoundException {
        Seminar seminar = seminarRepository.findById(seminarId)
                .orElseThrow(() -> new SeminarNotFoundException(seminarId));

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberNotFoundException(memberId));

        // check if the member is already linked to the seminar
        if (!seminar.getMembers().contains(member) || !member.getSeminars().contains(seminar)) {
            return ResponseEntity.notFound().build();
        }

        seminar.getMembers().remove(member);
        member.getSeminars().remove(seminar);

        seminarRepository.save(seminar);

        return ResponseEntity.noContent().build();
    }
}
