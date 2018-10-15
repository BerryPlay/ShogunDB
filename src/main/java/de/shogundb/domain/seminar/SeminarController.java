package de.shogundb.domain.seminar;

import de.shogundb.domain.member.Member;
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
import java.util.List;

@RestController
@RequestMapping(value = "/seminar")
public class SeminarController {
    private final SeminarRepository seminarRepository;
    private final MemberRepository memberRepository;
    private final PersonRepository personRepository;

    @Autowired
    public SeminarController(SeminarRepository seminarRepository, MemberRepository memberRepository, PersonRepository personRepository) {
        this.seminarRepository = seminarRepository;
        this.memberRepository = memberRepository;
        this.personRepository = personRepository;
    }

    @GetMapping
    public ResponseEntity<Iterable<Seminar>> index() {
        return ResponseEntity.ok(this.seminarRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Seminar> store(@RequestBody @Valid SeminarRegisterDTO seminarRegisterDTO) throws MemberNotFoundException, PersonNotFoundException {
        // get the members
        List<Member> members = new ArrayList<>();
        for (Long memberId : seminarRegisterDTO.getMembers()) {
            members.add(this.memberRepository.findById(memberId)
                    .orElseThrow(() -> new MemberNotFoundException(memberId)));
        }

        // get the referents
        List<Person> referents = new ArrayList<>();
        for (Long personId : seminarRegisterDTO.getReferents()) {
            referents.add(this.personRepository.findById(personId)
                    .orElseThrow(() -> new PersonNotFoundException(personId)));
        }

        // create the seminar
        Seminar seminar = this.seminarRepository.save(Seminar.builder()
                .name(seminarRegisterDTO.getName())
                .place(seminarRegisterDTO.getPlace())
                .dateFrom(seminarRegisterDTO.getDateFrom())
                .dateTo(seminarRegisterDTO.getDateTo())
                .seminarType(seminarRegisterDTO.getSeminarType())
                .build());

        // set members and referents
        seminar.setMembers(members);
        seminar.setReferents(referents);

        // link members to the seminar
        for (Member member : members) {
            member.getSeminars().add(seminar);
            System.out.println("Saved for member: " + member.getForename());
        }

        // link referents to seminar
        for (Person referent : referents) {
            referent.getSeminars().add(seminar);
            System.out.println("Saved for referent: " + referent.getName());
        }

        seminar = this.seminarRepository.save(seminar);

        URI uri = MvcUriComponentsBuilder.fromController(getClass()).path("/seminar/")
                .buildAndExpand(seminar.getId()).toUri();

        return ResponseEntity.created(uri).body(seminar);
    }
}
