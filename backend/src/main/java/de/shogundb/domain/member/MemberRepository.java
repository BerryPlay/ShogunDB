package de.shogundb.domain.member;

import de.shogundb.domain.contributionClass.ContributionClass;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends PagingAndSortingRepository<Member, Long> {

    Optional<Member> findById(Long id);

    List<Member> findByForenameContaining(String forename);

    List<Member> findBySurnameContaining(String surname);

    List<Member> findByForenameContainingOrSurnameContainingOrderBySurname(String forename, String surname);

    List<Member> findByContributionClass(ContributionClass contributionClass);

    @Query(
            value = "SELECT * FROM member m WHERE CONCAT(LOWER(m.forename), ' ', LOWER(m.surname)) " +
                    "LIKE concat('%', LOWER(:name), '%')",
            nativeQuery = true)
    List<Member> findByFullname(@Param("name") String name);
}
