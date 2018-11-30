package de.shogundb.domain.graduation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/exam")
public class ExamController {
    private final ExamRepository examRepository;

    @Autowired
    public ExamController(ExamRepository examRepository) {
        this.examRepository = examRepository;
    }

    /**
     * Get a list of all exams from the database.
     *
     * @return a list of all exams
     */
    @GetMapping
    public ResponseEntity<Iterable<Exam>> index() {
        return ResponseEntity.ok(examRepository.findAll());
    }
}