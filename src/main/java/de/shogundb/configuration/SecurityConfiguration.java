package de.shogundb.configuration;

import de.shogundb.authentication.TokenFilter;
import de.shogundb.authentication.UserDetailsServiceImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
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

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/token").permitAll()
                .antMatchers(HttpMethod.HEAD,"/token/**").permitAll()
                .antMatchers(HttpMethod.POST, "/user").permitAll()
                .anyRequest().authenticated()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // apply the token filter to the request
        http.addFilterBefore(tokenFilter, BasicAuthenticationFilter.class);

        http.cors();
    }

    @Override
    public UserDetailsService userDetailsService() {
        return this.userDetailsService;
    }
}
