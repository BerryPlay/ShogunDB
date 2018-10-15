package de.shogundb.domain.event;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface EventRepository extends PagingAndSortingRepository<Event, Long> {
    Optional<Event> findById(Long id);
}
