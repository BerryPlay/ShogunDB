package de.shogundb.domain.graduation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.shogundb.domain.BaseEntity;
import de.shogundb.domain.discipline.Discipline;
import lombok.*;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Graduation extends BaseEntity {
    /**
     * The unique identifier of the graduation.
     * Will be automatically generated.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * The name of the graduation (e.g. the 1. Kyu).
     */
    @NotBlank
    private String name;

    /**
     * The belt color (if exists).
     */
    private String color;

    /**
     * The condition which a member must fulfill to be admitted to the exam.
     */
    private String examConditions;

    /**
     * The condition which a member must fulfill to be shown in the list of members, who can possibly fulfill the real
     * conditions in the near future.
     */
    private String highlightConditions;

    /**
     * The discipline where the graduation belongs to.
     */
    @ManyToOne(cascade = CascadeType.ALL)
    private Discipline discipline;

    /**
     * A list of links of all members which obtained the graduation at an exam.
     */
    @OneToMany(orphanRemoval = true, mappedBy = "graduation")
    @JsonIgnore
    @Builder.Default
    private List<GraduationMember> graduationMembers = new ArrayList<>();
}