package de.shogundb.domain.exam;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface ExamRepository extends PagingAndSortingRepository<Exam, Long> {
    Optional<Exam> findById(Long id);
}
