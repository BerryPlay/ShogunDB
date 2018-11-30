package de.shogundb.domain.seminar;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.shogundb.domain.BaseEntity;
import de.shogundb.domain.member.Member;
import de.shogundb.domain.person.Person;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Seminar extends BaseEntity {
    /**
     * ID of the new seminar.
     * Will be automatically generated.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * Name of the new seminar.
     * Can't be null and must have a minimum of one and a maximum of 200 character.
     */
    @NotNull
    @Size(min = 1, max = 200)
    private String name;

    /**
     * Place of the new seminar.
     * Can't be null and must have a minimum of one and a maximum of 200 character.
     */
    @NotNull
    @Size(min = 1, max = 200)
    private String place;

    /**
     * Date of the new seminar, when the seminar starts.
     * Can't be null.
     */
    @NotNull
    private LocalDate dateFrom;

    /**
     * Date of the new seminar, when the seminar ends. If the seminar is only one day long, the dateTo is the same as
     * the dateFrom.
     * Can't be null.
     */
    @NotNull
    private LocalDate dateTo;

    /**
     * Type of the seminar. Can be LOCAL, REGIONAL, NATION or GLOBAL.
     * Can't be null.
     */
    @NotNull
    private SeminarType seminarType;

    /**
     * A list of ids (of persons) of all referents of the seminar.
     * Can't be null.
     */
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

    /**
     * A list of ids of all members of the seminar.
     * Can't be null.
     */
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
