package de.shogundb.domain.seminar;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.shogundb.domain.BaseEntity;
import de.shogundb.domain.member.Member;
import de.shogundb.domain.person.Person;
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
public class Seminar extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(min = 1, max = 200)
    private String name;

    @NotNull
    @Size(min = 1, max = 200)
    private String place;

    @NotNull
    private Date dateFrom;

    @NotNull
    private Date dateTo;

    @NotNull
    private SeminarType seminarType;

    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.REFRESH})
    @JoinTable(
            joinColumns = {@JoinColumn(name = "seminar_id")},
            inverseJoinColumns = {@JoinColumn(name = "person_id")})
    @JsonIgnore
    @Builder.Default
    private List<Person> referents = new ArrayList<>();

    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.REFRESH})
    @JoinTable(
            joinColumns = {@JoinColumn(name = "seminar_id")},
            inverseJoinColumns = {@JoinColumn(name = "member_id")})
    @JsonIgnore
    @Builder.Default
    private List<Member> members = new ArrayList<>();
}
