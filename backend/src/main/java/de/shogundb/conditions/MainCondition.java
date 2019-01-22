package de.shogundb.conditions;

import de.shogundb.conditions.statements.Condition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MainCondition {
    @NotNull
    Condition condition;

    public String getSQLQuery() {
        var query = new StringBuilder();

        // member.id, member.forename, member.surname, member.date_of_birth
        query.append("SELECT * FROM member WHERE (");
        query.append(condition.getSQLStatement());
        query.append(")");

        return query.toString();
    }
}
