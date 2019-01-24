package de.shogundb.conditions.statements;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.shogundb.conditions.DatabaseType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Adds an AND operation with a list of conditions which all must be true.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonDeserialize(as = AndCondition.class)
public class AndCondition implements Condition {
    @Builder.Default
    private List<Condition> conditions = new ArrayList<>();

    @Override
    public String getSQLStatement(DatabaseType databaseType) {
        var query = new StringBuilder();

        query.append("(");
        if (conditions.size() > 0) {
            // append all sql statements
            boolean firstPassed = false;

            // add an 'OR' if it is not the first run
            for (var condition : conditions) {
                if (firstPassed) {
                    query.append(" AND ");
                } else {
                    firstPassed = true;
                }
                query.append(condition.getSQLStatement(databaseType));
            }
        } else {
            query.append(1);
        }
        query.append(")");

        return query.toString();
    }
}