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

    //------------------- get all users ------------------------------------------------------------
    @GetMapping
    ResponseEntity<Collection<User>> getAllUsers() {
        return ResponseEntity.ok((Collection<User>) userRepository.findAll());
    }

    //------------------- call a user by id --------------------------------------------------------
    @GetMapping(value = "/{id}")
    ResponseEntity<User> getUserById(@PathVariable Long id) throws UserNotFoundException {
        return userRepository.findById(id).map(ResponseEntity::ok).orElseThrow(() -> new UserNotFoundException(id));
    }

    //------------------- check, if user exist by id -----------------------------------------------
    @RequestMapping(value = "/{id}", method = RequestMethod.HEAD)
    ResponseEntity<?> head(@PathVariable Long id) throws UserNotFoundException {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent()
                        .build());
    }

    //------------------- add a new user -----------------------------------------------------------
    @PostMapping
    ResponseEntity<User> addUser(@RequestBody @Valid UserRegister user) {
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

    //------------------- update an existing user --------------------------------------------------
    @PutMapping
    ResponseEntity<User> updateUser(@RequestBody @Valid UserUpdate user, HttpServletRequest request) throws UserNotFoundException {
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
                                        "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*" +
                                        "|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01" +
                                        "-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a" +
                                        "-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\" +
                                        ".){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x0" +
                                        "8\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-" +
                                        "\\x7f])+)\\])")) {
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

        return  ResponseEntity.badRequest().build();
    }

    //------------------- delete an existing user --------------------------------------------------
    @DeleteMapping(value = "/{id}")
    ResponseEntity<?> deleteUserById(@PathVariable Long id, HttpServletRequest request) throws UserNotFoundException {
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
