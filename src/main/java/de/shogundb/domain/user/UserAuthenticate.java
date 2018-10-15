package de.shogundb.domain.user;

import lombok.Data;

@Data
public class UserAuthenticate {
    private String username;
    private String password;
}
