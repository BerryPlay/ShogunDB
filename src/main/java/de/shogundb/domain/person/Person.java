package de.shogundb.domain.person;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.shogundb.domain.graduation.Exam;
import de.shogundb.domain.seminar.Seminar;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(min = 4, max = 200)
    private String name;

    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.REFRESH},
            mappedBy = "referents")
    @Builder.Default
    private List<Seminar> seminars = new ArrayList<>();

    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.REFRESH},
            mappedBy = "examiners")
    @JsonIgnore
    @Builder.Default
    private List<Exam> exams = new ArrayList<>();
}
