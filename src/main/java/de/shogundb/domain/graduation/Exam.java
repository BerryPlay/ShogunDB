package de.shogundb.domain.graduation;

import de.shogundb.domain.person.Person;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Exam {
    /**
     * The unique identifier of the seminar.
     * Will be automatically generated.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * The date of the exam.
     */
    @NotNull
    private Date date;

    /**
     * All graduations (connections between a member and a graduation).
     */
    @OneToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.REFRESH},
            mappedBy = "exam")
    @Builder.Default
    private List<GraduationMember> graduationMember = new ArrayList<>();

    /**
     * All examiners of the exam.
     */
    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.REFRESH})
    @JoinTable(name = "exam_person",
            joinColumns = {@JoinColumn(name = "exam_id")},
            inverseJoinColumns = {@JoinColumn(name = "person_id")})
    @Builder.Default
    private List<Person> examiners = new ArrayList<>();
}
