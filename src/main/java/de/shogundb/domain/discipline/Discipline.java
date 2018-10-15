package de.shogundb.domain.discipline;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.shogundb.domain.BaseEntity;
import de.shogundb.domain.graduation.Graduation;
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
public class Discipline extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(min = 1, max = 200)
    private String name;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "discipline")
    @JsonIgnore
    @Builder.Default
    private List<Graduation> graduations = new ArrayList<>();

    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.REFRESH})
    @JsonIgnore
    @Builder.Default
    private List<Member> members = new ArrayList<>();
}