package de.shogundb.domain.member;

import de.shogundb.domain.contributionClass.ContributionClass;
import de.shogundb.domain.contributionClass.ContributionClassNotFoundException;
import de.shogundb.domain.contributionClass.ContributionClassRepository;
import de.shogundb.domain.discipline.Discipline;
import de.shogundb.domain.discipline.DisciplineNotFoundException;
import de.shogundb.domain.discipline.DisciplineRepository;
import de.shogundb.domain.event.Event;
import de.shogundb.domain.event.EventNotFoundException;
import de.shogundb.domain.event.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/member")
public class MemberController {
    private final MemberRepository memberRepository;
    private final ContributionClassRepository contributionClassRepository;
    private final DisciplineRepository disciplineRepository;
    private final EventRepository eventRepository;

    @Autowired
    public MemberController(MemberRepository memberRepository, ContributionClassRepository contributionClassRepository, DisciplineRepository disciplineRepository, EventRepository eventRepository) {
        this.memberRepository = memberRepository;
        this.contributionClassRepository = contributionClassRepository;
        this.disciplineRepository = disciplineRepository;
        this.eventRepository = eventRepository;
    }

    @GetMapping
    public ResponseEntity<Iterable<Member>> index() {
        return ResponseEntity.ok(this.memberRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Member> store(@RequestBody @Valid MemberRegisterDTO member) throws ContributionClassNotFoundException, DisciplineNotFoundException {
        // setup contribution class
        ContributionClass contributionClass = null;
        if (member.getContributionClass() != null) {
            contributionClass = this.contributionClassRepository.findById(member.getContributionClass())
                    .orElseThrow(() -> new ContributionClassNotFoundException(member.getContributionClass()));
        }

        // setup disciplines
        List<Discipline> disciplines = new ArrayList<>();
        if (member.getDisciplines() != null && member.getDisciplines().size() >= 1) {
            for (Long disciplineId : member.getDisciplines()) {
                disciplines.add(this.disciplineRepository.findById(disciplineId)
                        .orElseThrow(() -> new DisciplineNotFoundException(disciplineId)));
            }
        }

        Member newMember = Member.builder()
                .forename(member.getForename())
                .surname(member.getSurname())
                .gender(member.getGender())
                .street(member.getStreet())
                .postcode(member.getPostcode())
                .phoneNumber(member.getPhoneNumber())
                .mobileNumber(member.getMobileNumber())
                .email(member.getEmail())
                .dateOfBirth(member.getDateOfBirth())
                .hasBudoPass(member.getHasBudoPass())
                .budoPassDate(member.getBudoPassDate())
                .enteredDate(member.getEnteredDate())
                .hasLeft(member.getHasLeft())
                .leftDate(member.getLeftDate())
                .isPassive(member.getIsPassive())
                .accountHolder(member.getAccountHolder())
                .build();

        newMember = this.memberRepository.save(newMember);

        if (contributionClass != null) {
            newMember.setContributionClass(contributionClass);
            contributionClass.getMembers().add(newMember);
        }

        if (disciplines.size() >= 1) {
            for (Discipline discipline : disciplines) {
                discipline.getMembers().add(newMember);
            }
        }
        newMember.getDisciplines().addAll(disciplines);

        newMember = this.memberRepository.save(newMember);

        URI uri = MvcUriComponentsBuilder.fromController(getClass()).path("/{id}")
                .buildAndExpand(newMember.getId()).toUri();

        return ResponseEntity.created(uri).body(newMember);
    }

    @PutMapping
    public ResponseEntity<Member> update(@RequestBody @Valid MemberUpdateDTO member) throws MemberNotFoundException, ContributionClassNotFoundException, DisciplineNotFoundException, EventNotFoundException {
        // get existing member from database
        Member existingMember = this.memberRepository.findById(member.getId())
                .orElseThrow(() -> new MemberNotFoundException(member.getId()));
        final Member finalMember = existingMember;

        // setup contribution class
        ContributionClass contributionClass = null;
        if (member.getContributionClass() != null) {
            contributionClass = this.contributionClassRepository.findById(member.getContributionClass())
                    .orElseThrow(() -> new ContributionClassNotFoundException(member.getContributionClass()));
            contributionClass.getMembers().add(existingMember);
        }

        // remove member from the contribution class
        if (existingMember.getContributionClass() != null) {
            existingMember.getContributionClass().getMembers().remove(existingMember);
        }
        existingMember.setContributionClass(contributionClass);

        // setup disciplines
        List<Discipline> disciplines = new ArrayList<>();
        if (member.getDisciplines() != null && member.getDisciplines().size() >= 1) {
            // add the required disciplines to the list
            for (Long disciplineId : member.getDisciplines()) {
                disciplines.add(this.disciplineRepository.findById(disciplineId)
                        .orElseThrow(() -> new DisciplineNotFoundException(disciplineId)));
            }
        }

        // apply member and disciplines
        existingMember.getDisciplines().forEach(existing -> existing.getMembers().remove(finalMember));
        existingMember.setDisciplines(disciplines);
        for (Discipline discipline : disciplines) {
            discipline.getMembers().add(existingMember);
        }

        // setup events
        List<Event> events = new ArrayList<>();
        if (member.getEvents() != null && member.getEvents().size() >= 1) {
            // add the required events to the list
            for (Long eventId : member.getEvents()) {
                events.add(this.eventRepository.findById(eventId)
                        .orElseThrow(() -> new EventNotFoundException(eventId)));
            }
        }

        // apply member and events
        existingMember.getEvents().forEach(existing -> existing.getMembers().remove(finalMember));
        existingMember.setEvents(events);
        for (Event event : events) {
            event.getMembers().add(existingMember);
        }

        existingMember.setForename(member.getForename());
        existingMember.setSurname(member.getSurname());
        existingMember.setGender(member.getGender());
        existingMember.setStreet(member.getStreet());
        existingMember.setPostcode(member.getPostcode());
        existingMember.setPhoneNumber(member.getPhoneNumber());
        existingMember.setMobileNumber(member.getMobileNumber());
        existingMember.setEmail(member.getEmail());
        existingMember.setDateOfBirth(member.getDateOfBirth());
        existingMember.setHasBudoPass(member.getHasBudoPass());
        existingMember.setBudoPassDate(member.getBudoPassDate());
        existingMember.setEnteredDate(member.getEnteredDate());
        existingMember.setHasLeft(member.getHasLeft());
        existingMember.setLeftDate(member.getLeftDate());
        existingMember.setIsPassive(member.getIsPassive());
        existingMember.setAccountHolder(member.getAccountHolder());
        existingMember.setNotes(member.getNotes());

        existingMember = this.memberRepository.save(existingMember);

        // return the updated member
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentRequest().toUriString());
        return ResponseEntity.created(uri).body(existingMember);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) throws MemberNotFoundException {
        Member member = this.memberRepository.findById(id).orElseThrow(() -> new MemberNotFoundException(id));

        // remove events
        for (Event event : member.getEvents()) {
            event.getMembers().remove(member);
        }
        member.getEvents().clear();

        // remove disciplines
        for (Discipline discipline : member.getDisciplines()) {
            discipline.getMembers().remove(member);
        }
        member.getEvents().clear();

        member.getContributionClass().getMembers().remove(member);
        member.setContributionClass(null);

        member = this.memberRepository.save(member);
        this.memberRepository.delete(member.getId());

        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Member> findById(@PathVariable Long id) throws MemberNotFoundException {
        Member member = this.memberRepository.findById(id).orElseThrow(() -> new MemberNotFoundException(id));
        return ResponseEntity.ok(member);
    }

    @GetMapping(value = "/byName/{name}")
    public ResponseEntity<Collection<Member>> findMembersByName(@PathVariable String name) {
        return ResponseEntity.ok(this.memberRepository.findByFullname(name));
    }
}
