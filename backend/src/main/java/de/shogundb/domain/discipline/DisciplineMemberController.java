package de.shogundb.domain.discipline;

import de.shogundb.domain.member.Member;
import de.shogundb.domain.member.MemberNotFoundException;
import de.shogundb.domain.member.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/discipline/member")
public class DisciplineMemberController {
    private final DisciplineRepository disciplineRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public DisciplineMemberController(DisciplineRepository disciplineRepository, MemberRepository memberRepository) {
        this.disciplineRepository = disciplineRepository;
        this.memberRepository = memberRepository;
    }

    @GetMapping("/{disciplineId}")
    public ResponseEntity<List<Member>> index(@PathVariable Long disciplineId) throws DisciplineNotFoundException {
        return ResponseEntity.ok(this.disciplineRepository.findById(disciplineId).map(Discipline::getMembers)
                .orElseThrow(() -> new DisciplineNotFoundException(disciplineId)));
    }

    @PostMapping(value = "/{disciplineId}/{memberId}")
    public ResponseEntity<Member> store(@PathVariable Long disciplineId, @PathVariable Long memberId) throws DisciplineNotFoundException, MemberNotFoundException {
        Discipline discipline = this.disciplineRepository.findById(disciplineId)
                .orElseThrow(() -> new DisciplineNotFoundException(disciplineId));
        Member member = this.memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        discipline.getMembers().add(member);
        member.getDisciplines().add(discipline);

        member = this.memberRepository.save(member);

        // TODO: test, if the path works
        URI uri = MvcUriComponentsBuilder.fromController(getClass()).path("/{id}")
                .buildAndExpand(member.getId()).toUri();
        return ResponseEntity.created(uri).body(member);
    }

    @DeleteMapping(value = "/{disciplineId}/{memberId}")
    public ResponseEntity<List<Member>> delete(@PathVariable Long disciplineId, @PathVariable Long memberId) throws DisciplineNotFoundException, MemberNotFoundException {
        Discipline discipline = this.disciplineRepository.findById(disciplineId)
                .orElseThrow(() -> new DisciplineNotFoundException(disciplineId));

        Member member = this.memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        // check member and events relation
        if (!member.getDisciplines().contains(discipline) || !discipline.getMembers().contains(member)) {
            return ResponseEntity.status(409).build();
        }

        discipline.getMembers().remove(member);
        member.getDisciplines().remove(discipline);

        return ResponseEntity.ok(this.disciplineRepository.save(discipline).getMembers());
    }
}
