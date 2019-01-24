package de.shogundb.conditions.statements;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.shogundb.conditions.DatabaseType;
import de.shogundb.conditions.PeriodFormat;
import de.shogundb.domain.seminar.SeminarType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * Adds a condition with the quantity of seminars of a special type a member must have visited within a period of time.
 * TODO: fix this condition to work with h2 and mysql
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonDeserialize(as = SeminarCondition.class)
public class SeminarCondition implements Condition {
    /**
     * The minimum number of results needed.
     */
    @NotNull
    private int quantity;

    /**
     * The type of the seminar.
     */
    @NotNull
    private SeminarType seminarType;

    /**
     * The time period which must be at elapsed since the last x days/month/years.
     */
    @NotNull
    private int period;

    /**
     * The quantifier of the time period (DAY, MONTH, YEAR).
     */
    @NotNull
    private PeriodFormat periodFormat;

    @Override
    public String getSQLStatement(DatabaseType databaseType) {
        return new StringBuilder()
                .append("(")
                .append(quantity)
                .append(" = (SELECT COUNT(seminar_members.member_id) FROM seminar ")
                .append("INNER JOIN seminar_members ON (seminar.id = seminar_members.seminar_id)")
                .append(" WHERE ")
                .append("seminar.date_to < (NOW() - INTERVAL ")
                .append(period)
                .append(" ")
                .append(periodFormat)
                .append(") AND seminar_members.member_id = member.id AND seminar.seminar_type = '")
                .append(seminarType)
                .append("' LIMIT ")
                .append(quantity)
                .append("))")
                .toString();
    }
}
