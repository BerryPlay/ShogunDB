package de.shogundb.domain.graduation;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Builder
@Getter
@Setter
public class ExamRegisterDTO {
    /**
     * The date of the exam.
     */
    @NotNull
    private Date date;

    /**
     * All graduations (connections between a member and a graduation).
     */
    @Builder.Default
    private List<GraduationMemberRegisterDTO> graduationMember;

    /**
     * A list with the ids of all examiners (persons).
     */
    @Builder.Default
    private List<Long> examiners;
}
