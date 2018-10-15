package de.shogundb.domain.contributionClass;

import de.shogundb.domain.member.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/contributionClass")
public class ContributionClassController {
    private final ContributionClassRepository contributionClassRepository;

    @Autowired
    public ContributionClassController(ContributionClassRepository contributionClassRepository) {
        this.contributionClassRepository = contributionClassRepository;
    }

    @GetMapping
    public ResponseEntity<Iterable<ContributionClass>> index() {
        return ResponseEntity.ok(this.contributionClassRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<ContributionClass> store(@RequestBody @Valid ContributionClass contributionClass) {
        ContributionClass newContributionClass = this.contributionClassRepository.save(contributionClass);

        URI uri = MvcUriComponentsBuilder.fromController(getClass()).path("/{id}")
                .buildAndExpand(newContributionClass.getId()).toUri();

        return ResponseEntity.created(uri).body(newContributionClass);
    }

    @PutMapping
    public ResponseEntity<ContributionClass> update(@RequestBody @Valid ContributionClass contributionClass) throws ContributionClassNotFoundException {
        return this.contributionClassRepository.findById(contributionClass.getId()).map(
                existing -> {
                    existing.setName(contributionClass.getName());
                    existing.setBaseContribution(contributionClass.getBaseContribution());
                    existing.setAdditionalContribution(contributionClass.getAdditionalContribution());

                    existing = this.contributionClassRepository.save(existing);

                    URI uri = MvcUriComponentsBuilder.fromController(getClass()).path("/{id}")
                            .buildAndExpand(existing.getId()).toUri();
                    return ResponseEntity.created(uri).body(existing);
                }).orElseThrow(() -> new ContributionClassNotFoundException(contributionClass.getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) throws ContributionClassNotFoundException {
        return this.contributionClassRepository.findById(id).map(
                existing -> {
                    // set contribution class id to null for every member of the contribution class
                    List<Member> members = existing.getMembers();
                    members.forEach(member -> member.setContributionClass(null));
                    members.clear();
                    this.contributionClassRepository.save(existing);

                    // delete the contribution class
                    this.contributionClassRepository.delete(existing.getId());
                    return ResponseEntity.noContent().build();
                }).orElseThrow(() -> new ContributionClassNotFoundException(id));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ContributionClass> show(@PathVariable Long id) throws ContributionClassNotFoundException {
        return ResponseEntity.ok(this.contributionClassRepository.findById(id)
                .orElseThrow(() -> new ContributionClassNotFoundException(id)));
    }
}
