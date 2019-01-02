package de.shogundb.domain.member;

import de.shogundb.domain.contributionClass.ContributionClassNotFoundException;
import de.shogundb.domain.contributionClass.ContributionClassRepository;
import de.shogundb.domain.discipline.Discipline;
import de.shogundb.domain.discipline.DisciplineNotFoundException;
import de.shogundb.domain.discipline.DisciplineRepository;
import de.shogundb.domain.event.Event;
import de.shogundb.domain.event.EventNotFoundException;
import de.shogundb.domain.event.EventRepository;
import de.shogundb.domain.graduation.GraduationMemberRepository;
import de.shogundb.domain.seminar.Seminar;
import de.shogundb.domain.seminar.SeminarNotFoundException;
import de.shogundb.domain.seminar.SeminarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

@RestController
@RequestMapping("/member")
public class MemberController {
    private final ContributionClassRepository contributionClassRepository;
    private final DisciplineRepository disciplineRepository;
    private final EventRepository eventRepository;
    private final GraduationMemberRepository graduationMemberRepository;
    private final MemberRepository memberRepository;
    private final SeminarRepository seminarRepository;

    @Autowired
    public MemberController(
            ContributionClassRepository contributionClassRepository,
            DisciplineRepository disciplineRepository,
            EventRepository eventRepository,
            GraduationMemberRepository graduationMemberRepository,
            MemberRepository memberRepository,
            SeminarRepository seminarRepository) {
        this.contributionClassRepository = contributionClassRepository;
        this.disciplineRepository = disciplineRepository;
        this.eventRepository = eventRepository;
        this.memberRepository = memberRepository;
        this.graduationMemberRepository = graduationMemberRepository;
        this.seminarRepository = seminarRepository;
    }

    /**
     * Get a list of all members in the database.
     *
     * @return a HTTP 200 OK and a list of all members
     */
    @GetMapping
    public ResponseEntity<Iterable<Member>> index() {
        return ResponseEntity.ok(this.memberRepository.findAll());
    }

    /**
     * Adds a new member to the database.
     *
     * @param member a data transfer object with all necessary information.
     * @return a HTTP 201 CREATED if the member was added successfully to the database.
     * @throws ContributionClassNotFoundException thrown, if no contribution with the given id exists
     * @throws DisciplineNotFoundException        thrown, if no discipline with the given ids exists
     */
    @PostMapping
    public ResponseEntity<Member> store(@RequestBody @Valid MemberRegisterDTO member)
            throws ContributionClassNotFoundException, DisciplineNotFoundException {
        // setup contribution class
        var contributionClass = this.contributionClassRepository.findById(member.getContributionClass())
                .orElseThrow(() -> new ContributionClassNotFoundException(member.getContributionClass()));

        // setup disciplines
        var disciplines = new ArrayList<Discipline>() {{
            for (var disciplineId : member.getDisciplines()) {
                add(disciplineRepository.findById(disciplineId)
                        .orElseThrow(() -> new DisciplineNotFoundException(disciplineId)));
            }
        }};

        var newMember = Member.builder()
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
                .contributionClass(contributionClass)
                .accountHolder(member.getAccountHolder())
                .build();

        // apply the member to the contribution class
        contributionClass.getMembers().add(newMember);

        // add the member to the disciplines
        for (var discipline : disciplines) {
            discipline.getMembers().add(newMember);
        }
        newMember.getDisciplines().addAll(disciplines);

        // save the member to the database
        newMember = this.memberRepository.save(newMember);

        var uri = MvcUriComponentsBuilder.fromController(getClass()).path("/{id}")
                .buildAndExpand(newMember.getId()).toUri();

        return ResponseEntity.created(uri).body(newMember);
    }

    /**
     * Updates an existing member in the database.
     *
     * @param member a data transfer object with all necessary information
     * @return a HTTP 201 CREATED if the member was updated successfully
     * @throws MemberNotFoundException            thrown, if no member with the given id exists
     * @throws ContributionClassNotFoundException thrown, if no contribution class with the given id exists
     * @throws DisciplineNotFoundException        thrown, if no discipline with the given ids exists
     * @throws EventNotFoundException             thrown, if no event with the given ids exists
     * @throws SeminarNotFoundException           thrown, if no seminar with the given ids exists
     */
    @PutMapping
    public ResponseEntity<Member> update(@RequestBody @Valid MemberUpdateDTO member) throws
            MemberNotFoundException,
            ContributionClassNotFoundException,
            DisciplineNotFoundException,
            EventNotFoundException,
            SeminarNotFoundException {
        // get existing member from database
        var existingMember = this.memberRepository.findById(member.getId())
                .orElseThrow(() -> new MemberNotFoundException(member.getId()));
        final var finalMember = existingMember;

        // setup contribution class
        var contributionClass = this.contributionClassRepository.findById(member.getContributionClass())
                .orElseThrow(() -> new ContributionClassNotFoundException(member.getContributionClass()));
        contributionClass.getMembers().add(existingMember);

        // remove member from the contribution class
        existingMember.getContributionClass().getMembers().remove(existingMember);
        existingMember.setContributionClass(contributionClass);

        // setup disciplines
        var disciplines = new ArrayList<Discipline>() {{
            // add the required disciplines to the list
            for (Long disciplineId : member.getDisciplines()) {
                add(disciplineRepository.findById(disciplineId)
                        .orElseThrow(() -> new DisciplineNotFoundException(disciplineId)));
            }
        }};

        // apply member and disciplines
        existingMember.getDisciplines().forEach(existing -> existing.getMembers().remove(finalMember));
        existingMember.setDisciplines(disciplines);
        for (var discipline : disciplines) {
            discipline.getMembers().add(existingMember);
        }

        // setup events
        var events = new ArrayList<Event>() {{
            // add the required events to the list
            for (Long eventId : member.getEvents()) {
                add(eventRepository.findById(eventId)
                        .orElseThrow(() -> new EventNotFoundException(eventId)));
            }
        }};

        // apply member and events
        existingMember.getEvents().forEach(existing -> existing.getMembers().remove(finalMember));
        existingMember.setEvents(events);
        for (var event : events) {
            event.getMembers().add(existingMember);
        }

        // setup seminars
        var seminars = new ArrayList<Seminar>() {{
            for (var seminarId : member.getSeminars()) {
                add(seminarRepository.findById(seminarId).orElseThrow(() -> new SeminarNotFoundException(seminarId)));
            }
        }};

        // apply member and seminars
        existingMember.getSeminars().forEach(existing -> existing.getMembers().remove(finalMember));
        existingMember.setSeminars(seminars);
        for (var seminar : seminars) {
            seminar.getMembers().add(existingMember);
        }

        // set the values
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

        // update the member in the database
        existingMember = this.memberRepository.save(existingMember);

        var uri = URI.create(ServletUriComponentsBuilder.fromCurrentRequest().toUriString());

        // return the updated member
        return ResponseEntity.created(uri).body(existingMember);
    }

    /**
     * Removes the member with the given id from the database.
     *
     * @param id the unique identifier of the member
     * @return a HTTP 204 NO CONTENT if the member was removed successfully
     * @throws MemberNotFoundException thrown, if no member with the given id exists
     */
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) throws MemberNotFoundException {
        var member = this.memberRepository.findById(id).orElseThrow(() -> new MemberNotFoundException(id));

        // remove events
        for (var event : member.getEvents()) {
            event.getMembers().remove(member);
        }
        member.getEvents().clear();

        // remove disciplines
        for (var discipline : member.getDisciplines()) {
            discipline.getMembers().remove(member);
        }
        member.getDisciplines().clear();

        // remove seminars
        for (var seminar : member.getSeminars()) {
            seminar.getMembers().remove(member);
        }
        member.getSeminars().clear();

        // remove the graduation member links
        for (var graduationMember : member.getGraduations()) {
            graduationMember.getExam().getGraduationMembers().remove(graduationMember);
            graduationMember.getGraduation().getGraduationMembers().remove(graduationMember);

            // delete the link in the database
            graduationMemberRepository.delete(graduationMember);
        }
        member.getGraduations().clear();

        // remove contribution class
        member.getContributionClass().
                getMembers().
                remove(member);
        member.setContributionClass(null);

        member = this.memberRepository.save(member);
        this.memberRepository.delete(member);

        return ResponseEntity.noContent().

                build();

    }

    /**
     * Returns the member with the given id.
     *
     * @param id the unique identifier of the member
     * @return a HTTP 200 OK and the member with the given id
     * @throws MemberNotFoundException thrown, if no member with the given id exists
     */
    @GetMapping(value = "/{id}")
    public ResponseEntity<Member> show(@PathVariable Long id) throws MemberNotFoundException {
        Member member = this.memberRepository.findById(id).orElseThrow(() -> new MemberNotFoundException(id));
        return ResponseEntity.ok(member);
    }

    /**
     * Returns a list of all members which matches the given forename and surname string.
     *
     * @param name a concat of the forename and surname
     * @return a HTTP 200 OK and a list of all members matching the given name
     */
    @GetMapping(value = "/byName/{name}")
    public ResponseEntity<Collection<Member>> findMembersByName(@PathVariable String name) {
        return ResponseEntity.ok(this.memberRepository.findByFullname(name));
    }
}
