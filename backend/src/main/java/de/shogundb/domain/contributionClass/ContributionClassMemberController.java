package de.shogundb.domain.contributionClass;

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
@RequestMapping(value = "/contributionClass/member")
public class ContributionClassMemberController {
    private final ContributionClassRepository contributionClassRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public ContributionClassMemberController(ContributionClassRepository contributionClassRepository, MemberRepository memberRepository) {
        this.contributionClassRepository = contributionClassRepository;
        this.memberRepository = memberRepository;
    }

    @GetMapping(value = "/{contributionClassId}")
    public ResponseEntity<List<Member>> index(@PathVariable Long contributionClassId) throws ContributionClassNotFoundException {
        return this.contributionClassRepository.findById(contributionClassId).map(
                existing -> ResponseEntity.ok(this.memberRepository.findByContributionClass(existing)))
                .orElseThrow(() -> new ContributionClassNotFoundException(contributionClassId));
    }

    @PostMapping(value = "/{contributionClassId}/{memberId}")
    public ResponseEntity<Member> store(@PathVariable Long contributionClassId, @PathVariable Long memberId) throws ContributionClassNotFoundException, MemberNotFoundException {
        ContributionClass contributionClass = this.contributionClassRepository
                .findById(contributionClassId)
                .orElseThrow(() -> new ContributionClassNotFoundException(contributionClassId));

        Member member = this.memberRepository
                .findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        // check, if member is already in the contribution class
        if (contributionClass.getMembers().stream()
                .anyMatch(m -> m.getId().equals(memberId))) {
            return ResponseEntity.status(409).body(null);
        }

        // remove member from old contribution class (if exists)
        if (member.getContributionClass() != null) {
            ContributionClass oldContributionClass = this.contributionClassRepository
                    .findById(member.getContributionClass().getId())
                    .orElseThrow(() -> new ContributionClassNotFoundException(member.getContributionClass().getId()));
            oldContributionClass.getMembers().remove(member);
            this.contributionClassRepository.save(oldContributionClass);
        }

        contributionClass.getMembers().add(member);
        member.setContributionClass(contributionClass);
        this.contributionClassRepository.save(contributionClass);

        URI uri = MvcUriComponentsBuilder.fromController(getClass()).path("/{contributionClassId}")
                .buildAndExpand(member.getId()).toUri();

        return ResponseEntity.created(uri).body(member);
    }

    @DeleteMapping(value = "/{contributionClassId}/{memberId}")
    public ResponseEntity<Member> delete(@PathVariable Long contributionClassId, @PathVariable Long memberId) throws ContributionClassNotFoundException, MemberNotFoundException {
        ContributionClass contributionClass = this.contributionClassRepository
                .findById(contributionClassId)
                .orElseThrow(() -> new ContributionClassNotFoundException(contributionClassId));

        Member member = this.memberRepository
                .findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        if (!contributionClass.getMembers().contains(member) || !member.getContributionClass().equals(contributionClass)) {
            return ResponseEntity.status(409).build();
        }

        // remove member from contribution class
        contributionClass.getMembers().remove(member);

        // remove contribution class from member
        member.setContributionClass(null);

        member = this.memberRepository.save(member);

        return ResponseEntity.ok(member);
    }
}
