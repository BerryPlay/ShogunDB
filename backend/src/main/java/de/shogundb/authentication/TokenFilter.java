package de.shogundb.authentication;

import de.shogundb.domain.token.Token;
import de.shogundb.domain.token.TokenRepository;
import de.shogundb.domain.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;

@Component
public class TokenFilter extends OncePerRequestFilter {
    private final TokenRepository tokenRepository;
    private final AuthenticationManager authenticationManager;

    @Autowired
    @Lazy
    public TokenFilter(TokenRepository tokenRepository, AuthenticationManager authenticationManager) {
        this.tokenRepository = tokenRepository;
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // get the token from the headers
        final String header = request.getHeader("Authorization");

        // return if the token doesn't exist
        if (header == null) {
            filterChain.doFilter(request, response);
            return;
        }

        final String plaintToken = header.replace("Bearer ", "");

        Optional<Token> tokenOptional = this.tokenRepository.findByToken(plaintToken);

        // return if the token doesn't exist
        if (!tokenOptional.isPresent()) {
            filterChain.doFilter(request, response);
            return;
        }

        Token token = tokenOptional.get();

        // return if token is already expired
        if (new Date().after(token.getExpireDate())) {
            // delete the expired token
            tokenRepository.delete(token);

            filterChain.doFilter(request, response);
            return;
        }

        User user = token.getUser();

        Authentication authenticationToken = new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                user.getPassword());

        SecurityContextHolder.getContext().setAuthentication(authenticationManager.authenticate(authenticationToken));

        filterChain.doFilter(request, response);
    }
}
