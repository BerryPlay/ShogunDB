package de.shogundb.domain.exam;

import de.shogundb.domain.graduation.GraduationMemberRegisterDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
public class ExamRegisterDTO {
    /**
     * The unique identifier of the exam (only set if it already exists in the database).
     */
    private Long id;

    /**
     * The date of the exam.
     */
    @NotNull
    private LocalDate date;

    /**
     * All graduations (connections between a member and a graduation).
     */
    @Builder.Default
    private List<GraduationMemberRegisterDTO> graduationMembers = new ArrayList<>();

    /**
     * A list with the ids of all examiners (persons).
     */
    @Builder.Default
    private List<Long> examiners = new ArrayList<>();
}
