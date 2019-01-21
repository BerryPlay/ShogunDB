package de.shogundb.domain.user;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class UserRegister {
    @NotNull
    @Size(min = 4, max = 200)
    private String username;

    @NotNull
    @Size(min = 4)
    private String password;

    @NotNull
    @Size(min = 4, max = 200)
    @Pattern(regexp = "^[A-Za-z0-9_.]+@[A-Za-z0-9_.]+\\.[A-Za-z0-9_.]+$")
    private String email;
}
