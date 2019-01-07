package de.shogundb.domain.seminar;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface SeminarRepository extends PagingAndSortingRepository<Seminar, Long> {
    Optional<Seminar> findById(Long id);
}
