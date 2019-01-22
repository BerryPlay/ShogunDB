package de.shogundb.conditions.statements;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

/**
 * Adds an OR operation with a list of conditions statements at least one must be true.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonDeserialize(as = OrCondition.class)
public class OrCondition implements Condition {
    @Builder.Default
    @NotEmpty
    private List<Condition> conditions = new ArrayList<>();

    @Override
    public String getSQLStatement() {
        var query = new StringBuilder();

        query.append("(");
        if (conditions.size() > 0) {
            // append all sql statements
            boolean firstPassed = false;

            // add an 'OR' if it is not the first run
            for (var condition : conditions) {
                if (firstPassed) {
                    query.append(" OR ");
                } else {
                    firstPassed = true;
                }
                query.append(condition.getSQLStatement());
            }
        } else {
            query.append(0);
        }
        query.append(")");

        return query.toString();
    }
}
