package de.shogundb.conditions.statements;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.shogundb.conditions.PeriodFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * Adds a condition with the period of time a member must be a member.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonDeserialize(as = MembershipCondition.class)
public class MembershipCondition implements Condition {
    /**
     * The time period which must be elapsed since the entering.
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
                .append("(member.entered_date < NOW() - INTERVAL ")
                .append(period)
                .append(" ")
                .append(periodFormat)
                .append(")")
                .toString();
    }
}
