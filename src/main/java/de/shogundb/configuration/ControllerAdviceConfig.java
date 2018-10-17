package de.shogundb.configuration;

import de.shogundb.domain.championship.ChampionshipMemberNotFoundException;
import de.shogundb.domain.championship.ChampionshipNotFoundException;
import de.shogundb.domain.contributionClass.ContributionClassNotFoundException;
import de.shogundb.domain.discipline.DisciplineNotFoundException;
import de.shogundb.domain.event.EventNotFoundException;
import de.shogundb.domain.graduation.GraduationNotFoundException;
import de.shogundb.domain.member.MemberNotFoundException;
import de.shogundb.domain.seminar.SeminarNotFoundException;
import de.shogundb.domain.user.UserNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ControllerAdviceConfig extends ResponseEntityExceptionHandler {
    @ExceptionHandler(ChampionshipNotFoundException.class)
    public ResponseEntity<String> handleChampionshipNotFoundException(ChampionshipNotFoundException e) {
        return ResponseEntity.status(409).body("Championship does not exist!");
    }

    @ExceptionHandler(ChampionshipMemberNotFoundException.class)
    public ResponseEntity<String> handleChampionshipMemberNotFoundException(ChampionshipMemberNotFoundException e) {
        return ResponseEntity.status(409).body("Championship member association does not exist!");
    }

    @ExceptionHandler(ContributionClassNotFoundException.class)
    public ResponseEntity<String> handleContributionClassNotFoundException(ContributionClassNotFoundException e) {
        return ResponseEntity.status(409).body("Contribution class does not exist!");
    }

    @ExceptionHandler(DisciplineNotFoundException.class)
    public ResponseEntity<String> handleDisciplineNotFoundException(DisciplineNotFoundException e) {
        return ResponseEntity.status(409).body("Discipline does not exist!");
    }

    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<String> handleEventNotFoundException(EventNotFoundException e) {
        return ResponseEntity.status(409).body("Event does not exist!");
    }

    @ExceptionHandler(GraduationNotFoundException.class)
    public ResponseEntity<String> handleGraduationNotFoundException(GraduationNotFoundException e) {
        return ResponseEntity.status(409).body("Graduation does not exist!");
    }

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<String> handleContributionClassNotFoundException(MemberNotFoundException e) {
        return ResponseEntity.status(409).body("Member does not exist!");
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException e) {
        return ResponseEntity.status(409).body("User does not exist!");
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<String> handleSecurityException(SecurityException e) {
        return ResponseEntity.status(409).body("Incorrect auth header present");
    }

    @ExceptionHandler(SeminarNotFoundException.class)
    public ResponseEntity<?> handleSeminarNotFoundException(SeminarNotFoundException e) {
        return ResponseEntity.notFound().build();
    }
}
