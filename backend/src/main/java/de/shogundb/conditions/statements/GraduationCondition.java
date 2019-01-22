package de.shogundb.conditions.statements;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.shogundb.conditions.PeriodFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * Adds a condition with the time which must be elapsed since the exam of the given graduation.
 * TODO: add a graduation condition to check, if the member doesn't has an exam of a special graduation
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonDeserialize(as = GraduationCondition.class)
public class GraduationCondition implements Condition {
    /**
     * The unique identifier of the target graduation.
     */
    @NotNull
    private Long id;

    /**
     * The time period which must be elapsed since the exam.
     */
    @NotNull
    private int period;

    /**
     * The quantifier of the time period (DAY, MONTH, YEAR).
     */
    @NotNull
    private PeriodFormat periodFormat;

    @Override
    public String getSQLStatement() {
        return new StringBuilder()
                .append("(1 = (SELECT COUNT(*) FROM graduation ")
                .append("INNER JOIN graduation_member ON (graduation.id = graduation_member.graduation_id) ")
                .append("INNER JOIN exam ON (graduation_member.exam_id = exam.id) ")
                .append("WHERE graduation.id = ")
                .append(id)
                .append(" AND graduation_member.member_id = member.id AND exam.date < NOW() - INTERVAL ")
                .append(period)
                .append(" ")
                .append(periodFormat)
                .append(" LIMIT 1))")
                .toString();
    }
}
