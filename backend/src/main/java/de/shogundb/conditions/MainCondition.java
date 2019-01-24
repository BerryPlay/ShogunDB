package de.shogundb.conditions;

import de.shogundb.conditions.statements.Condition;
import de.shogundb.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EntityManager;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MainCondition {
    /**
     * A condition object (it is possible to chain multiple condition objects together using the AndCondition or
     * OrCondition.
     */
    @NotNull
    Condition condition;

    /**
     * Builds the query by executing all `getSQLStatement()` methods from the conditions recursively.
     *
     * @param databaseType the type of the database (H2 or MYSQL)
     * @return a complete sql query with all conditions in the where clause
     */
    public String getSQLQuery(DatabaseType databaseType) {
        return new StringBuilder()
                .append("SELECT * FROM member WHERE (")
                .append(condition.getSQLStatement(databaseType))
                .append(")")
                .toString();
    }

    /**
     * Returns a list of all members matching the condition.
     *
     * @param entityManager an entity manager (@PersistenceContext) to execute a native query against the database
     * @param databaseType  the type of the database (H2 or MYSQL)
     * @return a list of all members matching the condition
     */
    public List<Member> getMembers(EntityManager entityManager, DatabaseType databaseType) {
        // create the query (with all conditions recursively)
        var query = entityManager.createNativeQuery(getSQLQuery(databaseType), Member.class);

        // fetch and return the results
        return new ArrayList<>() {{
            for (var member : query.getResultList()) {
                add((Member) member);
            }
        }};
    }
}
