package de.shogundb.domain.contributionClass;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.shogundb.domain.BaseEntity;
import de.shogundb.domain.member.Member;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ContributionClass extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(min = 1, max = 200)
    private String name;

    @NotNull
    private double baseContribution;

    @NotNull
    private double additionalContribution;

    @OneToMany(orphanRemoval = true, mappedBy = "contributionClass")
    @JsonIgnore
    @Builder.Default
    List<Member> members = new ArrayList<>();
}
