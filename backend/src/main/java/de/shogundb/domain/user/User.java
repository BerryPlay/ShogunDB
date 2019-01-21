package de.shogundb.domain.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.shogundb.domain.BaseEntity;
import de.shogundb.domain.token.Token;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(min = 4, max = 200)
    private String username;

    @NotNull
    @Size(min = 4)
    @JsonIgnore
    private String password;

    @NotNull
    @Size(min = 4, max = 200)
    @Pattern(regexp = "^[A-Za-z0-9_.]+@[A-Za-z0-9_.]+\\.[A-Za-z0-9_.]+$")
    private String email;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL)
    @Builder.Default
    private List<Token> token = new ArrayList<>();

    public User(
            @NotNull @Size(min = 4, max = 200) String username,
            @NotNull @Size(min = 1) String password,
            @NotNull @Size(min = 4, max = 200)
            @Pattern(regexp = "^[A-Za-z0-9_.]+@[A-Za-z0-9_.]+\\.[A-Za-z0-9_.]+$") String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }
}
