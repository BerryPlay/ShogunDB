package de.shogundb.domain.championship;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.shogundb.domain.BaseEntity;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Championship extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(min = 1, max = 200)
    private String name;

    @NotNull
    private Date date;

    @OneToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.REFRESH},
            mappedBy = "championship")
    @NotNull
    @JsonIgnore
    @Builder.Default
    private List<ChampionshipMember> members = new ArrayList<>();
}
