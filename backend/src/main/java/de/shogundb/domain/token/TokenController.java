package de.shogundb.domain.token;

import de.shogundb.domain.user.User;
import de.shogundb.domain.user.UserAuthenticate;
import de.shogundb.domain.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/token")
public class TokenController {
    private final TokenRepository tokenRepository;
    private final TokenService tokenService;
    private final UserRepository userRepository;

    @Autowired
    public TokenController(TokenRepository tokenRepository, TokenService tokenService, UserRepository userRepository) {
        this.tokenRepository = tokenRepository;
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<String> store(@RequestBody UserAuthenticate userAuthenticate) {
        // load user from database, if exists
        User user = this.userRepository
                .findByUsername(userAuthenticate.getUsername())
                .orElseThrow(SecurityException::new);

        // check, if password is valid
        if (!BCrypt.checkpw(userAuthenticate.getPassword(), user.getPassword())) {
            throw new SecurityException();
        }

        return ResponseEntity.ok(this.tokenService.generate(user).getToken());
    }

    /**
     * Removes the given token from the database.
     *
     * @param token the token to remove
     * @return a HTTP 204 NO CONTENT if the token was removed successfully
     * @throws TokenNotFoundException thrown, if the given token does not exists
     */
    @DeleteMapping("/{token}")
    public ResponseEntity<?> delete(@PathVariable String token) throws TokenNotFoundException {
        var existingToken = tokenRepository.findByToken(token).orElseThrow(() -> new TokenNotFoundException(token));

        // unlink the token from the user
        existingToken.getUser().getToken().remove(existingToken);

        // remove the token from the database
        tokenRepository.delete(existingToken);

        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/{token}", method = RequestMethod.HEAD)
    public ResponseEntity<?> validate(@PathVariable String token) {
        // check if the token exists
        if (this.tokenRepository.findByToken(token).isPresent()) {
            Token checkToken = this.tokenRepository.findByToken(token).get();

            // check if token is already expired
            if (new Date().before(checkToken.getExpireDate())) {
                return ResponseEntity.ok().build();
            }
        }
        return ResponseEntity.status(401).build();
    }
}
