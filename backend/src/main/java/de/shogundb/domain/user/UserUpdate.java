package de.shogundb.domain.user;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UserUpdate {
    @NotNull
    private Long id;

    private String password;
    private String email;
}
