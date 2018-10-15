package de.shogundb.domain.championship;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.shogundb.domain.member.Member;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class ChampionshipMember {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(cascade = {
            CascadeType.PERSIST,
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.REFRESH})
    @NotNull
    @JsonIgnore
    private Member member;

    @ManyToOne(cascade = {
            CascadeType.PERSIST,
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.REFRESH})
    @NotNull
    private Championship championship;

    private int rank;

    @Size(min = 1, max = 200)
    private String extra;
}
