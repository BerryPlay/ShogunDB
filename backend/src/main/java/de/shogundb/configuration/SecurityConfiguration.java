package de.shogundb.configuration;

import de.shogundb.authentication.TokenFilter;
import de.shogundb.authentication.UserDetailsServiceImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    private final TokenFilter tokenFilter;
    private final UserDetailsServiceImplementation userDetailsService;

    @Autowired
    public SecurityConfiguration(TokenFilter tokenFilter, UserDetailsServiceImplementation userDetailsService) {
        this.tokenFilter = tokenFilter;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Configures which routes will not be covered by the security chain. Also enables cross-origin access.
     *
     * @param http the http security object
     * @throws Exception thrown, if any kind of error appears
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()
                .authorizeRequests()

                // permit all get requests for the frontend
                .antMatchers(HttpMethod.GET, "/").permitAll()
                .antMatchers(HttpMethod.GET, "/css/**").permitAll()
                .antMatchers(HttpMethod.GET, "/js/**").permitAll()
                .antMatchers(HttpMethod.GET, "/img/**").permitAll()
                .antMatchers(HttpMethod.GET, "/fonts/**").permitAll()
                .antMatchers(HttpMethod.GET, "/favicon.ico").permitAll()
                .antMatchers(HttpMethod.GET, "/favicon.svg").permitAll()

                // permit the authentication routes
                .antMatchers(HttpMethod.POST, "/token").permitAll()
                .antMatchers(HttpMethod.HEAD, "/token/**").permitAll()
                .antMatchers(HttpMethod.POST, "/user").permitAll()

                // permit the initial setup routes
                .antMatchers(HttpMethod.HEAD, "/user/exists").permitAll()
                .anyRequest().authenticated()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // apply the token filter to the request
        http.addFilterBefore(tokenFilter, BasicAuthenticationFilter.class);

        http.cors();
    }

    /**
     * Provides a user detail service.
     *
     * @return a user detail service
     */
    @Override
    public UserDetailsService userDetailsService() {
        return this.userDetailsService;
    }

    /**
     * Provides a authentication manager bean (used to authenticate the user).
     *
     * @return an authentication bean
     * @throws Exception thrown, if any kind of errors appears while authenticating
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * Define the password encoder to used.
     *
     * @return a password encoder
     */
    @Bean
    public NoOpPasswordEncoder passwordEncoder() {
        return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
    }

}
