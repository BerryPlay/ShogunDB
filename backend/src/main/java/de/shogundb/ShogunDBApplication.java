package de.shogundb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
@EnableJpaAuditing
public class ShogunDBApplication {
    /**
     * Runs the spring application.
     *
     * @param args the application arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(ShogunDBApplication.class, args);
    }

    /**
     * Configures the cross origin access.
     *
     * @return a WebMvcConfigurerAdapter to overwrite the cors configuration
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS")
                        .allowedOrigins("*");
            }
        };
    }

    /**
     * An own implementation of the h2 'DATEDIFF' method (which doesn't work properly). Calculates the period of time
     * between date one and two.
     *
     * @param unit  the unit ('YEARS', 'MONTH' or 'DAYS')
     * @param date1 the lower date
     * @param date2 the upper date
     * @return the number of days/months/years between the two dates
     */
    public static long period(String unit, java.sql.Date date1, java.sql.Date date2) {
        return java.time.temporal.ChronoUnit.valueOf(unit).between(date1.toLocalDate(), date2.toLocalDate());
    }
}
