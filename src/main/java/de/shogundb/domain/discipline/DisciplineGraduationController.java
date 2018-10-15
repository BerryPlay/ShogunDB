package de.shogundb.domain.discipline;

import de.shogundb.domain.graduation.Graduation;
import de.shogundb.domain.graduation.GraduationNotFoundException;
import de.shogundb.domain.graduation.GraduationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/discipline/graduation")
public class DisciplineGraduationController {

    private final GraduationRepository graduationRepository;
    private final DisciplineRepository disciplineRepository;

    @Autowired
    public DisciplineGraduationController(GraduationRepository graduationRepository, DisciplineRepository disciplineRepository) {
        this.graduationRepository = graduationRepository;
        this.disciplineRepository = disciplineRepository;
    }

    @GetMapping(value = "/{disciplineId}")
    public ResponseEntity<List<Graduation>> index(@PathVariable Long disciplineId) throws DisciplineNotFoundException {
        return this.disciplineRepository.findById(disciplineId).
                map(existing -> ResponseEntity.ok(existing.getGraduations()))
                .orElseThrow(() -> new DisciplineNotFoundException(disciplineId));
    }

    @PostMapping(value = "/{disciplineId}")
    public ResponseEntity<List<Graduation>> store(@RequestBody @Valid Graduation graduation, @PathVariable Long disciplineId) throws DisciplineNotFoundException {

        return this.disciplineRepository.findById(disciplineId).map(
                existing -> {
                    // set the graduations discipline
                    graduation.setDiscipline(existing);

                    existing.getGraduations().add(graduation);
                    Discipline discipline = this.disciplineRepository.save(existing);

                    URI uri = MvcUriComponentsBuilder.fromController(getClass()).path("/{id}")
                            .buildAndExpand(discipline.getId()).toUri();

                    return ResponseEntity.created(uri).body(discipline.getGraduations());
                }).orElseThrow(() -> new DisciplineNotFoundException(disciplineId));
    }

    @DeleteMapping(value = "/{graduationId}")
    public ResponseEntity<List<Graduation>> delete(@PathVariable Long graduationId) throws GraduationNotFoundException {
        Graduation graduation = this.graduationRepository.findById(graduationId)
                .orElseThrow(() -> new GraduationNotFoundException(graduationId));

        Discipline discipline = graduation.getDiscipline();

        discipline.getGraduations().remove(graduation);
        graduation.setDiscipline(null);

        return ResponseEntity.ok(this.disciplineRepository.save(discipline).getGraduations());
    }
}
