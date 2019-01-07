package de.shogundb.domain.seminar;

import de.shogundb.domain.person.Person;
import de.shogundb.domain.person.PersonNotFoundException;
import de.shogundb.domain.person.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/seminar/person")
public class SeminarPersonController {
    private final PersonRepository personRepository;
    private final SeminarRepository seminarRepository;

    @Autowired
    public SeminarPersonController(PersonRepository personRepository, SeminarRepository seminarRepository) {
        this.personRepository = personRepository;
        this.seminarRepository = seminarRepository;
    }

    /**
     * Get all referents of the seminar with the given id.
     *
     * @param seminarId the unique identifier of the seminar
     * @return a HTTP 200 OK and a list of all persons of the seminar
     * @throws SeminarNotFoundException thrown, if the seminar does not exist
     */
    @GetMapping("/{seminarId}")
    private ResponseEntity<List<Person>> index(@PathVariable Long seminarId) throws SeminarNotFoundException {
        Seminar seminar = seminarRepository.findById(seminarId)
                .orElseThrow(() -> new SeminarNotFoundException(seminarId));

        return ResponseEntity.ok(seminar.getReferents());
    }

    /**
     * Adds an existing person to an existing seminar.
     *
     * @param seminarId the unique identifier of the seminar
     * @param personId  the unique identifier of the person
     * @return a HTTP 201 CREATED if the person was successfully added to the seminar
     * @throws SeminarNotFoundException thrown, if the seminar does not exist
     * @throws PersonNotFoundException  thrown, if the person does not exist
     */
    @PostMapping("/{seminarId}/{personId}")
    private ResponseEntity<?> store(@PathVariable Long seminarId, @PathVariable Long personId)
            throws SeminarNotFoundException, PersonNotFoundException {
        Seminar seminar = seminarRepository.findById(seminarId)
                .orElseThrow(() -> new SeminarNotFoundException(seminarId));

        Person referent = personRepository.findById(personId).orElseThrow(() -> new PersonNotFoundException(personId));

        if (seminar.getReferents().contains(referent) || referent.getSeminars().contains(seminar)) {
            return ResponseEntity.status(409).build();
        }

        // link the referent and the seminar
        seminar.getReferents().add(referent);
        referent.getSeminars().add(seminar);

        seminar = seminarRepository.save(seminar);

        URI uri = MvcUriComponentsBuilder.fromController(getClass()).buildAndExpand(seminar.getId()).toUri();

        return ResponseEntity.created(uri).build();
    }

    /**
     * Removes the person with the given id from the seminar with the given id.
     *
     * @param seminarId the unique identifier of the seminar
     * @param personId  the unique identifier of the person
     * @return a HTTP 204 NO CONTENT if the person was successfully removed from the seminar
     * @throws SeminarNotFoundException thrown, if the seminar does not exist
     * @throws PersonNotFoundException  thrown, if the person does not exist
     */
    @DeleteMapping("/{seminarId}/{personId}")
    private ResponseEntity<?> delete(@PathVariable Long seminarId, @PathVariable Long personId)
            throws SeminarNotFoundException, PersonNotFoundException {
        Seminar seminar = seminarRepository.findById(seminarId)
                .orElseThrow(() -> new SeminarNotFoundException(seminarId));

        Person referent = personRepository.findById(personId).orElseThrow(() -> new PersonNotFoundException(personId));

        if (!seminar.getReferents().contains(referent) || !referent.getSeminars().contains(seminar)) {
            return ResponseEntity.notFound().build();
        }

        seminar.getReferents().remove(referent);
        referent.getSeminars().remove(seminar);

        seminarRepository.save(seminar);

        return ResponseEntity.noContent().build();
    }
}
