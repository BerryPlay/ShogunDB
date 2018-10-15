package de.shogundb.domain.token;

import de.shogundb.domain.user.User;
import de.shogundb.domain.user.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

@Service
public class TokenService {
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    @Autowired
    public TokenService(TokenRepository tokenRepository, UserRepository userRepository) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
    }

    public Token generate(User user) {
        final Calendar calendar = Calendar.getInstance();

        // add one day to the calendar (one day validity)
        calendar.add(Calendar.DAY_OF_MONTH, 1);

        return this.generate(user, calendar.getTime());
    }

    public Token generate(User user, Date expireDate) {
        // build token
        Token token = Token.builder()
                .token(generateToken())
                .expireDate(expireDate)
                .user(user)
                .build();

        // add token to the user
        user.getToken().add(token);

        tokenRepository.save(token);

        return token;
    }

    private String generateToken() {
        // generate a random string with the length of 1024
        String tokenString;
        do {
            tokenString = RandomStringUtils.randomAlphanumeric(1024, 1024);
        } while (tokenRepository.findByToken(tokenString).isPresent());

        return tokenString;
    }

    public User getUser(String token) {
        if (this.tokenRepository.findByToken(token).isPresent()) {
            return this.tokenRepository
                    .findByToken(token)
                    .get().getUser();
        }

        return null;
    }
}
