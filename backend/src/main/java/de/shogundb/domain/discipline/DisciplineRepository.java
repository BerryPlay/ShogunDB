package de.shogundb.domain.discipline;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface DisciplineRepository extends PagingAndSortingRepository<Discipline, Long> {
    Optional<Discipline> findById(Long id);
}
