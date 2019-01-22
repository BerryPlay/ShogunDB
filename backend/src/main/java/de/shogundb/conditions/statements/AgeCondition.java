package de.shogundb.conditions.statements;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * Adds a condition with the minimum age the member must have.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonDeserialize(as = AgeCondition.class)
public class AgeCondition implements Condition {
    /**
     * The age of the member.
     */
    @NotNull
    private int age;

    @Override
    public String getSQLStatement() {
        return "(member.date_of_birth < NOW() - INTERVAL " + age + " YEAR)";
    }
}
