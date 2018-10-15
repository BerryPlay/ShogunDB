package de.shogundb.domain.person;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface PersonRepository extends PagingAndSortingRepository<Person, Long> {
    Optional<Person> findById(Long id);
    Optional<Person> findByNameEquals(String name);
}
