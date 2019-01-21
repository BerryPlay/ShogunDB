package de.shogundb.domain.member;

import de.shogundb.domain.BaseEntity;
import de.shogundb.domain.championship.ChampionshipMember;
import de.shogundb.domain.contributionClass.ContributionClass;
import de.shogundb.domain.discipline.Discipline;
import de.shogundb.domain.event.Event;
import de.shogundb.domain.graduation.GraduationMember;
import de.shogundb.domain.seminar.Seminar;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(min = 1, max = 200)
    private String forename;

    @NotNull
    @Size(min = 1, max = 200)
    private String surname;

    @NotNull
    private Gender gender;

    @NotNull
    @Size(min = 1, max = 200)
    private String street;

    @NotNull
    @Size(min = 3, max = 8)
    private String postcode;

    @NotBlank
    @Size(min = 1, max = 200)
    private String city;

    @NotNull
    @Size(min = 4, max = 200)
    private String phoneNumber;

    @Size(min = 4, max = 200)
    private String mobileNumber;

    @NotNull
    @Size(min = 4, max = 200)
    @Pattern(regexp = "^[A-Za-z0-9_.]+@[A-Za-z0-9_.]+\\.[A-Za-z0-9_.]+$")
    private String email;

    @NotNull
    private LocalDate dateOfBirth;

    @NotNull
    private Boolean hasBudoPass;

    private LocalDate budoPassDate;

    @NotNull
    private LocalDate enteredDate;

    @Builder.Default
    private Boolean hasLeft = false;

    private LocalDate leftDate;

    @Builder.Default
    private Boolean isPassive = false;

    @ManyToOne
    private ContributionClass contributionClass;

    @NotNull
    @Size(min = 1, max = 200)
    private String accountHolder;

    @NotNull
    @Size(max = 1000)
    @Builder.Default
    private String notes = "";

    @OneToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.REFRESH},
            mappedBy = "member")
    @Builder.Default
    private List<ChampionshipMember> championships = new ArrayList<>();

    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.REFRESH},
            mappedBy = "members")
    @Builder.Default
    private List<Discipline> disciplines = new ArrayList<>();

    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.REFRESH},
            mappedBy = "members")
    @Builder.Default
    private List<Event> events = new ArrayList<>();

    @OneToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.REFRESH},
            mappedBy = "member")
    @Builder.Default
    private List<GraduationMember> graduations = new ArrayList<>();

    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.REFRESH},
            mappedBy = "members")
    @Builder.Default
    private List<Seminar> seminars = new ArrayList<>();
}
