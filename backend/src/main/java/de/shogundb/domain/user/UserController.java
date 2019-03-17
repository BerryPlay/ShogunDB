package de.shogundb.domain.user;

import de.shogundb.domain.token.Token;
import de.shogundb.domain.token.TokenRepository;
import de.shogundb.domain.token.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final TokenService tokenService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    public UserController(UserRepository userRepository, TokenRepository tokenRepository, TokenService tokenService) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.tokenService = tokenService;
    }

    /**
     * Get a list of all users in the database.
     *
     * @return a HTTP 200 OK and a list of all users
     */
    @GetMapping
    ResponseEntity<Collection<User>> index() {
        return ResponseEntity.ok((Collection<User>) userRepository.findAll());
    }

    /**
     * Adds a new user to the database.
     *
     * @param user a user data transfer object with all necessary information
     * @return a HTTP 201 CREATED and the new user, if the user was created successfully, or a HTTP 204 NO CONTENT, if a
     * user with the given username already exists
     */
    @PostMapping
    ResponseEntity<User> store(@RequestBody @Valid UserRegister user) {
        // check, if user already exists
        if (this.userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.noContent().build();
        }

        // save new user to the database
        User newUser = userRepository.save(
                User.builder()
                        .username(user.getUsername())
                        .password(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()))
                        .email(user.getEmail())
                        .token(new ArrayList<>())
                        .build()
        );

        URI uri = MvcUriComponentsBuilder.fromController(getClass()).path("/{id}")
                .buildAndExpand(newUser.getId()).toUri();


        return ResponseEntity.created(uri).body(newUser);
    }

    /**
     * Updates an existing user in the database.
     *
     * @param user    a data transfer object with all necessary information
     * @param request a servlet request object to check, if the current authenticated user is the same as the user to
     *                update
     * @return a HTTP 201 CREATED if the user was updated successfully
     * @throws UserNotFoundException thrown, if the given user does not exist in the database
     */
    @PutMapping
    ResponseEntity<User> update(@RequestBody @Valid UserUpdate user, HttpServletRequest request)
            throws UserNotFoundException {
        Long id = user.getId();

        if (id.equals(this.getAuthenticatedId(request))) {
            return this.userRepository.findById(id)
                    .map(
                            existing -> {
                                // check, if the password is valid
                                if (user.getPassword() != null && user.getPassword().length() >= 4) {
                                    existing.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
                                }

                                if (user.getEmail() != null && user.getEmail().matches(
                                        "^[A-Za-z0-9_.]+@[A-Za-z0-9_.]+\\.[A-Za-z0-9_.]+$")) {
                                    existing.setEmail(user.getEmail());
                                }

                                // save updated user
                                User updatedUser = this.userRepository.save(existing);

                                // return updated user
                                URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentRequest().toUriString());
                                return ResponseEntity.created(uri).body(updatedUser);
                            }
                    ).orElseThrow(() -> new UserNotFoundException(user.getId()));
        }

        return ResponseEntity.badRequest().build();
    }

    /**
     * Deletes the user with the given id and all its tokens from the database.
     *
     * @param id      the unique identifier of the user
     * @param request a servlet request object to check, if the current authenticated user is the same as the user to
     *                delete
     * @return a HTTP 204 NO CONTENT if the user was successfully removed from the database
     * @throws UserNotFoundException thrown, if no user with the given id exists
     */
    @DeleteMapping(value = "/{id}")
    ResponseEntity<?> delete(@PathVariable Long id, HttpServletRequest request) throws UserNotFoundException {
        // check if the id belongs to the authenticated user
        if (id.equals(this.getAuthenticatedId(request))) {
            return userRepository.findById(id).map(
                    existing -> {
                        userRepository.delete(existing);
                        return ResponseEntity.noContent().build();
                    }).orElseThrow(() -> new UserNotFoundException(id));
        }
        return ResponseEntity.badRequest().build();
    }

    /**
     * Returns the user with the given id (except of his password hash).
     *
     * @param id the unique identifier of the user
     * @return a HTTP 200 OK and the user with the given id
     * @throws UserNotFoundException thrown, if no user with the given id exists
     */
    @GetMapping(value = "/{id}")
    ResponseEntity<User> show(@PathVariable Long id) throws UserNotFoundException {
        return userRepository.findById(id).map(ResponseEntity::ok).orElseThrow(() -> new UserNotFoundException(id));
    }

    /**
     * Validates, if the user with the given id exists.
     *
     * @param id the unique identifier of the user
     * @return a HTTP 200 OK if the user with the given id exists, else a HTTP 204 NO CONTENT
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.HEAD)
    ResponseEntity<?> head(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent()
                        .build());
    }

    /**
     * An endpoint to check, if the database has at least one user.
     *
     * @return a HTTP 200 OK if at least one user exists in the database, else a HTTP 204 NO CONTENT
     */
    @RequestMapping(value = "/exists", method = RequestMethod.HEAD)
    ResponseEntity<?> checkIfAUserExists() {
        return userRepository.count() > 0 ? ResponseEntity.ok().build() : ResponseEntity.noContent().build();
    }

    /**
     * Returns the id of the current authenticated user.
     *
     * @param request a servlet request object to get the current authenticated user
     * @return the id of the current authenticated user
     */
    private Long getAuthenticatedId(HttpServletRequest request) {
        // get the token from the headers
        final String header = request.getHeader("Authorization");
        final String plaintToken = header.replace("Bearer ", "");

        Optional<Token> tokenOptional = this.tokenRepository.findByToken(plaintToken);
        Token token = tokenOptional.get();
        User user = token.getUser();

        return user.getId();
    }
}
