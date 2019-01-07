package de.shogundb.domain.championship;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface ChampionshipRepository extends PagingAndSortingRepository<Championship, Long> {
    Optional<Championship> findById(Long id);
}
