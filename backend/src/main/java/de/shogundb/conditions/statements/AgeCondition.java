package de.shogundb.conditions.statements;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.shogundb.conditions.DatabaseType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

import static de.shogundb.conditions.DatabaseType.H2;

/**
 * Adds a condition with the minimum minAge the member must have.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonDeserialize(as = AgeCondition.class)
public class AgeCondition implements Condition {
    /**
     * The minimum age of the member.
     */
    @NotNull
    private int minAge;

    /**
     * The maximum age of the member.
     */
    @NotNull
    private int maxAge;

    @Override
    public String getSQLStatement(DatabaseType databaseType) {
        var unit = databaseType == H2 ? "'YEARS'" : "YEAR";
        var diffMethod = databaseType == H2 ? "GET_DIFF" : "TIMESTAMPDIFF";
        if (minAge == maxAge) {
            return new StringBuilder()
                    .append("(")
                    .append(diffMethod)
                    .append("(")
                    .append(unit).append(", member.date_of_birth, CURDATE()) = ")
                    .append(minAge)
                    .append(")")
                    .toString();
        } else {
            return new StringBuilder()
                    .append("((")
                    .append(diffMethod)
                    .append("(")
                    .append(unit)
                    .append(", member.date_of_birth, CURDATE()) >= ")
                    .append(minAge)
                    .append(") AND (")
                    .append(diffMethod)
                    .append("(")
                    .append(unit)
                    .append(", member.date_of_birth, CURDATE()) <= ")
                    .append(maxAge)
                    .append("))")
                    .toString();
        }
    }
}
