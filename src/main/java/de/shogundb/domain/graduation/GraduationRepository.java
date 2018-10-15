package de.shogundb.domain.graduation;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Collection;
import java.util.Optional;

public interface GraduationRepository extends PagingAndSortingRepository<Graduation, Long> {
    Optional<Graduation> findById(Long id);
    Optional<Collection<Graduation>> findByName(String name);
    Optional<Collection<Graduation>> findByNameContaining(String name);
    Optional<Collection<Graduation>> findByColor(String color);
    Optional<Collection<Graduation>> findByColorContaining(String color);
}
