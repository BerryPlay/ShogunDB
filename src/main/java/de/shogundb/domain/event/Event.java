package de.shogundb.domain.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.shogundb.domain.BaseEntity;
import de.shogundb.domain.member.Member;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Event extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(min = 1, max = 200)
    private String name;

    @NotNull
    private Date date;

    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.REFRESH})
    @JoinTable(
            joinColumns = {@JoinColumn(name = "event_id")},
            inverseJoinColumns = {@JoinColumn(name = "member_id")})
    @JsonIgnore
    @Builder.Default
    private List<Member> members = new ArrayList<>();
}
