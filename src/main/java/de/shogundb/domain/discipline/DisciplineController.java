package de.shogundb.domain.discipline;

import de.shogundb.domain.graduation.GraduationRepository;
import de.shogundb.domain.member.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/discipline")
public class DisciplineController {
    private final GraduationRepository graduationRepository;
    private final DisciplineRepository disciplineRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public DisciplineController(GraduationRepository graduationRepository, DisciplineRepository disciplineRepository, MemberRepository memberRepository) {
        this.graduationRepository = graduationRepository;
        this.disciplineRepository = disciplineRepository;
        this.memberRepository = memberRepository;
    }

    @GetMapping
    public ResponseEntity<Iterable<Discipline>> index() {
        return ResponseEntity.ok(this.disciplineRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Discipline> store(@RequestBody @Valid Discipline discipline) {
        Discipline newDiscipline = this.disciplineRepository.save(discipline);

        URI uri = MvcUriComponentsBuilder.fromController(getClass()).path("/{id}")
                .buildAndExpand(newDiscipline.getId()).toUri();

        return ResponseEntity.created(uri).body(newDiscipline);
    }

    @PutMapping
    public ResponseEntity<Discipline> update(@RequestBody @Valid DisciplineUpdateDTO discipline) throws DisciplineNotFoundException {
        return this.disciplineRepository.findById(discipline.getId()).map(
                existing -> {
                    existing.setName(discipline.getName());
                    existing = this.disciplineRepository.save(existing);

                    URI uri = MvcUriComponentsBuilder.fromController(getClass()).path("/{id}")
                            .buildAndExpand(existing.getId()).toUri();
                    return ResponseEntity.created(uri).body(existing);
                }).orElseThrow(() -> new DisciplineNotFoundException(discipline.getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) throws DisciplineNotFoundException {
        return this.disciplineRepository.findById(id)
                .map(
                        existing -> {
                            // detach discipline from members
                            existing.getMembers().forEach(
                                    members -> {
                                        members.getDisciplines().remove(existing);
                                        this.memberRepository.save(members);
                                    });
                            this.disciplineRepository.delete(id);
                            return ResponseEntity.noContent().build();
                        }
                )
                .orElseThrow(() -> new DisciplineNotFoundException(id));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Discipline> show(@PathVariable Long id) throws DisciplineNotFoundException {
        Discipline discipline = this.disciplineRepository.findById(id)
                .orElseThrow(() -> new DisciplineNotFoundException(id));
        return ResponseEntity.ok(discipline);
    }
}
