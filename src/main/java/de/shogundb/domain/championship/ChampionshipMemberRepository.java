package de.shogundb.domain.championship;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface ChampionshipMemberRepository extends PagingAndSortingRepository<ChampionshipMember, Long> {
    Optional<ChampionshipMember> findById(Long id);
}
