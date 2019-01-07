package de.shogundb.domain.graduation;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface GraduationMemberRepository extends PagingAndSortingRepository<GraduationMember, Long> {
    Optional<GraduationMember> findById(long id);
}
