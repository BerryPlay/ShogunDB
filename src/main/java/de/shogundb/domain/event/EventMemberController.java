package de.shogundb.domain.event;

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
@RequestMapping(value = "/event/member")
public class EventMemberController {

    private final EventRepository eventRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public EventMemberController(EventRepository eventRepository, MemberRepository memberRepository) {
        this.eventRepository = eventRepository;
        this.memberRepository = memberRepository;
    }

    @GetMapping(value = "/{eventId}")
    public ResponseEntity<List<Member>> index(@PathVariable Long eventId) throws EventNotFoundException {
        return this.eventRepository.findById(eventId).map(
                existing -> ResponseEntity.ok(existing.getMembers()))
                .orElseThrow(() -> new EventNotFoundException(eventId));
    }

    @PostMapping(value = "/{eventId}/{memberId}")
    public ResponseEntity<List<Member>> store(@PathVariable Long eventId, @PathVariable Long memberId) throws EventNotFoundException, MemberNotFoundException {
        Event event = this.eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));

        Member member = this.memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        // check member and events relation
        if (member.getEvents().contains(event) || event.getMembers().contains(member)) {
            return ResponseEntity.status(409).build();
        }

        member.getEvents().add(event);
        event.getMembers().add(member);

        this.memberRepository.save(member);

        URI uri = MvcUriComponentsBuilder.fromController(getClass()).path("/event/member/")
                .buildAndExpand(event.getId()).toUri();

        return ResponseEntity.created(uri).body(event.getMembers());
    }

    @DeleteMapping(value = "/{eventId}/{memberId}")
    public ResponseEntity<List<Member>> delete(@PathVariable Long eventId, @PathVariable Long memberId) throws EventNotFoundException, MemberNotFoundException {
        Event event = this.eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));

        Member member = this.memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        // check member and events relation
        if (!member.getEvents().contains(event) || !event.getMembers().contains(member)) {
            return ResponseEntity.status(409).build();
        }

        member.getEvents().remove(event);
        event.getMembers().remove(member);

        return ResponseEntity.ok(this.eventRepository.save(event).getMembers());
    }
}
