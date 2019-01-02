package de.shogundb.domain.discipline;

import de.shogundb.domain.graduation.Graduation;
import de.shogundb.domain.graduation.GraduationMemberRepository;
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

    private final GraduationMemberRepository graduationMemberRepository;
    private final GraduationRepository graduationRepository;
    private final DisciplineRepository disciplineRepository;

    @Autowired
    public DisciplineGraduationController(
            GraduationMemberRepository graduationMemberRepository,
            GraduationRepository graduationRepository,
            DisciplineRepository disciplineRepository) {
        this.graduationMemberRepository = graduationMemberRepository;
        this.graduationRepository = graduationRepository;
        this.disciplineRepository = disciplineRepository;
    }

    /**
     * Get a list of all graduations from the discipline with the given id from the database.
     *
     * @param disciplineId the unique identifier of the discipline
     * @return all graduations from the discipline with the given id
     * @throws DisciplineNotFoundException thrown, if no discipline with the given id does exists
     */
    @GetMapping(value = "/{disciplineId}")
    public ResponseEntity<List<Graduation>> index(@PathVariable Long disciplineId) throws DisciplineNotFoundException {
        return this.disciplineRepository.findById(disciplineId).
                map(existing -> ResponseEntity.ok(existing.getGraduations()))
                .orElseThrow(() -> new DisciplineNotFoundException(disciplineId));
    }

    /**
     * Adds a new  graduation to the database for the discipline with the given id and links all given members, persons
     * and graduations to it.
     *
     * @param graduation   the graduation to store
     * @param disciplineId the unique identifier of the discipline
     * @return a HTTP 201 CREATED if the graduation was added successfully
     * @throws DisciplineNotFoundException thrown, if no discipline with the given id exists
     */
    @PostMapping(value = "/{disciplineId}")
    public ResponseEntity<List<Graduation>> store(
            @RequestBody @Valid Graduation graduation,
            @PathVariable Long disciplineId) throws DisciplineNotFoundException {

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

    /**
     * Removes the graduation with the given id.
     *
     * @param id the unique identifier of the graduation
     * @return a HTTP 204 NO CONTENT if the graduation was removed successfully
     * @throws GraduationNotFoundException thrown, if a graduation with the given id does not exists
     */
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<List<Graduation>> delete(@PathVariable Long id) throws GraduationNotFoundException {
        var graduation = graduationRepository.findById(id).orElseThrow(() -> new GraduationNotFoundException(id));

        // unlink from discipline
        graduation.getDiscipline().getGraduations().remove(graduation);
        graduation.setDiscipline(null);

        // remove all graduation member links
        for (var graduationMember : graduation.getGraduationMembers()) {
            graduationMember.getExam().getGraduationMembers().remove(graduationMember);
            graduationMember.getMember().getGraduations().remove(graduationMember);

            // delete the link in the database
            graduationMemberRepository.delete(graduationMember);
        }
        graduation.getGraduationMembers().clear();

        // remove the graduation from the database
        graduationRepository.delete(graduation);

        return ResponseEntity.noContent().build();
    }
}
