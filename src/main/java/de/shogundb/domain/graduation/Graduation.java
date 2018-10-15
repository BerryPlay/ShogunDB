package de.shogundb.domain.graduation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.shogundb.domain.BaseEntity;
import de.shogundb.domain.discipline.Discipline;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Graduation extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String name;

    private String color;

    private String examConditions;

    private String highlightConditions;

    @ManyToOne(cascade = CascadeType.ALL)
    @JsonIgnore
    private Discipline discipline;
}