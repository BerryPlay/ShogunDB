package de.shogundb.domain.graduation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.shogundb.domain.exam.Exam;
import de.shogundb.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GraduationMember {
    /**
     * The unique identifier of the graduation member link.
     * Will be automatically generated.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * The graduation which the member has achieved.
     */
    @NotNull
    @ManyToOne(cascade = {
            CascadeType.PERSIST,
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.REFRESH})
    private Graduation graduation;

    /**
     * The member who achieved the graduation.
     */
    @NotNull
    @ManyToOne(cascade = {
            CascadeType.PERSIST,
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.REFRESH})
    @JsonIgnore
    private Member member;

    /**
     * The exam where the member achieved the graduation.
     */
    @NotNull
    @ManyToOne(cascade = {
            CascadeType.PERSIST,
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.REFRESH})
    @JsonIgnore
    private Exam exam;

    /**
     * Helper method removes a graduation member link.
     *
     * @param graduationMember           the graduation member link to unlink
     * @param graduationMemberRepository the jpa repository object to handle the database access
     */
    public static void removeGraduationMember(
            GraduationMember graduationMember,
            GraduationMemberRepository graduationMemberRepository) {
        // unlink all
        graduationMember.getExam().getGraduationMembers().remove(graduationMember);
        graduationMember.getGraduation().getGraduationMembers().remove(graduationMember);
        graduationMember.getMember().getGraduations().remove(graduationMember);

        // delete the link in the database
        graduationMemberRepository.delete(graduationMember);
    }
}
