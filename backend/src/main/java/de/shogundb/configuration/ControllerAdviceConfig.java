package de.shogundb.configuration;

import de.shogundb.domain.championship.ChampionshipMemberNotFoundException;
import de.shogundb.domain.championship.ChampionshipNotFoundException;
import de.shogundb.domain.contributionClass.ContributionClassNotFoundException;
import de.shogundb.domain.discipline.DisciplineNotFoundException;
import de.shogundb.domain.event.EventNotFoundException;
import de.shogundb.domain.exam.ExamNotFoundException;
import de.shogundb.domain.graduation.GraduationMemberNotFoundException;
import de.shogundb.domain.graduation.GraduationNotFoundException;
import de.shogundb.domain.member.MemberNotFoundException;
import de.shogundb.domain.person.PersonNotFoundException;
import de.shogundb.domain.seminar.SeminarNotFoundException;
import de.shogundb.domain.token.TokenNotFoundException;
import de.shogundb.domain.user.UserNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ControllerAdviceConfig extends ResponseEntityExceptionHandler {
    @ExceptionHandler(ChampionshipNotFoundException.class)
    public ResponseEntity<?> handleChampionshipNotFoundException(ChampionshipNotFoundException e) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(ChampionshipMemberNotFoundException.class)
    public ResponseEntity<?> handleChampionshipMemberNotFoundException(ChampionshipMemberNotFoundException e) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(ContributionClassNotFoundException.class)
    public ResponseEntity<?> handleContributionClassNotFoundException(ContributionClassNotFoundException e) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(DisciplineNotFoundException.class)
    public ResponseEntity<?> handleDisciplineNotFoundException(DisciplineNotFoundException e) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<?> handleEventNotFoundException(EventNotFoundException e) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(ExamNotFoundException.class)
    public ResponseEntity<?> handleExamNotFoundException(ExamNotFoundException e) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(GraduationNotFoundException.class)
    public ResponseEntity<?> handleGraduationNotFoundException(GraduationNotFoundException e) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(GraduationMemberNotFoundException.class)
    public ResponseEntity<?> handleGraduationMemberNotFoundException(GraduationMemberNotFoundException e) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<?> handleMemberNotFoundException(MemberNotFoundException e) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(PersonNotFoundException.class)
    public ResponseEntity<?> handlePersonNotFoundException(PersonNotFoundException e) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<?> handleSecurityException(SecurityException e) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(SeminarNotFoundException.class)
    public ResponseEntity<?> handleSeminarNotFoundException(SeminarNotFoundException e) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity<?> handleTokenNotFoundException(TokenNotFoundException e) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFoundException(UserNotFoundException e) {
        return ResponseEntity.notFound().build();
    }
}
