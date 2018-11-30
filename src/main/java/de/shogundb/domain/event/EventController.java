package de.shogundb.domain.event;

import de.shogundb.domain.member.Member;
import de.shogundb.domain.member.MemberNotFoundException;
import de.shogundb.domain.member.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/event")
public class EventController {
    private final EventRepository eventRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public EventController(EventRepository eventRepository, MemberRepository memberRepository) {
        this.eventRepository = eventRepository;
        this.memberRepository = memberRepository;
    }

    @GetMapping
    public ResponseEntity<Iterable<Event>> index() {
        return ResponseEntity.ok(this.eventRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Event> store(@RequestBody @Valid EventRegisterDTO event) throws MemberNotFoundException {
        // get the members from the database
        List<Member> members = new ArrayList<>();
        for (Long id : event.getMembers()) {
            members.add(this.memberRepository.findById(id).orElseThrow(() -> new MemberNotFoundException(id)));
        }

        Event newEvent = Event.builder()
                .name(event.getName())
                .date(event.getDate())
                .build();

        newEvent = this.eventRepository.save(newEvent);

        newEvent.setMembers(members);

        // add the new event to the members
        for (Member member : members) {
            member.getEvents().add(newEvent);
        }

        newEvent = this.eventRepository.save(newEvent);

        URI uri = MvcUriComponentsBuilder.fromController(getClass()).path("/{id}")
                .buildAndExpand(newEvent.getId()).toUri();

        return ResponseEntity.created(uri).body(newEvent);
    }

    @PutMapping
    public ResponseEntity<Event> update(@RequestBody @Valid EventUpdateDTO event) throws EventNotFoundException, MemberNotFoundException {
        Event existingEvent = this.eventRepository.findById(event.getId())
                .orElseThrow(() -> new EventNotFoundException(event.getId()));

        // get members from the database
        List<Member> members = new ArrayList<>();
        for (Long id : event.getMembers()) {
            members.add(this.memberRepository.findById(id).orElseThrow(() -> new MemberNotFoundException(id)));
        }

        // detach the members from the events
        for (Member member : existingEvent.getMembers()) {
            member.getEvents().remove(existingEvent);
        }

        // attach the event to the members
        for (Member member : members) {
            member.getEvents().add(existingEvent);
        }

        existingEvent.setName(event.getName());
        existingEvent.setDate(event.getDate());
        existingEvent.setMembers(members);

        existingEvent = this.eventRepository.save(existingEvent);

        URI uri = MvcUriComponentsBuilder.fromController(getClass()).path("/{id}")
                .buildAndExpand(existingEvent.getId()).toUri();

        return ResponseEntity.created(uri).body(existingEvent);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) throws EventNotFoundException {
        return this.eventRepository.findById(id).map(
                existing -> {
                    // remove the event from all its members
                    existing.getMembers().forEach(existingMember -> existingMember.getEvents().remove(existing));
                    this.eventRepository.save(existing);

                    // delete event
                    this.eventRepository.delete(existing);
                    return ResponseEntity.noContent().build();
                }).orElseThrow(() -> new EventNotFoundException(id));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Event> findById(@PathVariable Long id) throws EventNotFoundException {
        return ResponseEntity.ok(this.eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id)));
    }
}
