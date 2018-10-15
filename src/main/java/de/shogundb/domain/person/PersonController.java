package de.shogundb.domain.person;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping(value = "/person")
public class PersonController {
    private final PersonRepository personRepository;

    @Autowired
    public PersonController(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @GetMapping
    public ResponseEntity<Iterable<Person>> index() {
        return ResponseEntity.ok().body(this.personRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Person> store(@RequestBody @Valid Person person) {
        // check, if a person with the name already exists
        if (this.personRepository.findByNameEquals(person.getName()).isPresent()) {
            return ResponseEntity.status(409).build();
        }

        // rebuild person (to apply the default definitions)
        person = Person.builder()
                .name(person.getName())
                .build();

        person = this.personRepository.save(person);

        URI uri = MvcUriComponentsBuilder.fromController(getClass()).path("/{id}")
                .buildAndExpand(person.getId()).toUri();

        return ResponseEntity.created(uri).body(person);
    }
}
