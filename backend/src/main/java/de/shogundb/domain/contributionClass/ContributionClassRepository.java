package de.shogundb.domain.contributionClass;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface ContributionClassRepository extends PagingAndSortingRepository<ContributionClass, Long> {
    Optional<ContributionClass> findById(Long id);
}
