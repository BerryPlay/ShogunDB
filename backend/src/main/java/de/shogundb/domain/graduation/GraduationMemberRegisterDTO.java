package de.shogundb.domain.graduation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GraduationMemberRegisterDTO {
    /**
     * The unique identifier of the graduation.
     */
    @NotNull
    private Long graduationId;

    /**
     * The unique identifier of the member.
     */
    @NotNull
    private Long memberId;

    /**
     * The unique identifier of the exam.
     * Can be left blank while creating a new exam but must be set if the exam already exists.
     */
    private Long examId;
}
