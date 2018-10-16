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
    public SeminarController(
            SeminarRepository seminarRepository,
            MemberRepository memberRepository,
            PersonRepository personRepository) {
        this.seminarRepository = seminarRepository;
        this.memberRepository = memberRepository;
        this.personRepository = personRepository;
    }

    /**
     * Get a list of all seminars from the database.
     *
     * @return a list of all seminars
     */
    @GetMapping
    public ResponseEntity<Iterable<Seminar>> index() {
        return ResponseEntity.ok(this.seminarRepository.findAll());
    }

    /**
     * Adds a new seminar to the database and links all given members and persons to it.
     *
     * @param seminarRegisterDTO a data transfer object with all necessary information
     * @return a HTTP 201 CREATED if the seminar was added successfully
     * @throws MemberNotFoundException thrown, if any of the given members are not in the database
     * @throws PersonNotFoundException thrown, if any of the given persons are not in the database
     */
    @PostMapping
    public ResponseEntity<Seminar> store(@RequestBody @Valid SeminarRegisterDTO seminarRegisterDTO)
            throws MemberNotFoundException, PersonNotFoundException {
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

    /**
     * Update an existing seminar und (un-)link all members and referents based on the given ids.
     *
     * @param seminarUpdateDTO a data transfer object with all necessary information
     * @return A HTTP 201 CREATED if the seminar was updated successfully
     * @throws MemberNotFoundException thrown, if any of the given members are not in the database
     * @throws PersonNotFoundException thrown, if any of the given persons are not in the database
     * @throws SeminarNotFoundException thrown, if the given seminar is not in the database
     */
    @PutMapping
    public ResponseEntity<Seminar> update(@RequestBody @Valid SeminarUpdateDTO seminarUpdateDTO)
            throws MemberNotFoundException, PersonNotFoundException, SeminarNotFoundException {
        // get the existing seminar
        Seminar existingSeminar = seminarRepository.findById(seminarUpdateDTO.getId())
                .orElseThrow(() -> new SeminarNotFoundException(seminarUpdateDTO.getId()));

        // get the members
        List<Member> members = new ArrayList<>();
        for (Long memberId : seminarUpdateDTO.getMembers()) {
            members.add(this.memberRepository.findById(memberId)
                    .orElseThrow(() -> new MemberNotFoundException(memberId)));
        }

        // get the referents
        List<Person> referents = new ArrayList<>();
        for (Long personId : seminarUpdateDTO.getReferents()) {
            referents.add(this.personRepository.findById(personId)
                    .orElseThrow(() -> new PersonNotFoundException(personId)));
        }

        // detach all members from the existing seminar
        existingSeminar.getMembers().forEach(member -> member.getSeminars().remove(existingSeminar));
        existingSeminar.getMembers().clear();

        // add the members
        existingSeminar.getMembers().addAll(members);
        members.forEach(member -> member.getSeminars().add(existingSeminar));

        // detach all persons from the existing seminar
        existingSeminar.getReferents().forEach(referent -> referent.getSeminars().remove(existingSeminar));
        existingSeminar.getReferents().clear();

        // add the referents
        existingSeminar.getReferents().addAll(referents);
        referents.forEach(referent -> referent.getSeminars().add(existingSeminar));

        // update the other values
        existingSeminar.setName(seminarUpdateDTO.getName());
        existingSeminar.setPlace(seminarUpdateDTO.getPlace());
        existingSeminar.setDateFrom(seminarUpdateDTO.getDateFrom());
        existingSeminar.setDateTo(seminarUpdateDTO.getDateTo());
        existingSeminar.setSeminarType(seminarUpdateDTO.getSeminarType());

        // save everything to the database
        Seminar updatedSeminar = seminarRepository.save(existingSeminar);

        // generate uri to the updated seminar
        URI uri = MvcUriComponentsBuilder.fromController(getClass()).path("/{id}")
                .buildAndExpand(updatedSeminar.getId()).toUri();

        return ResponseEntity.created(uri).body(updatedSeminar);
    }
}
