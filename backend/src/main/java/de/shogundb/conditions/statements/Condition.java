package de.shogundb.conditions.statements;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.shogundb.conditions.ConditionDeserializer;
import de.shogundb.conditions.DatabaseType;

/**
 * An interface to get all statements conditions for a sql query.
 */
@JsonDeserialize(using = ConditionDeserializer.class)
public interface Condition {
    /**
     * Returns the SQL Statement.
     *
     * @return a sql statement, based on the attributes of the condition
     */
    String getSQLStatement(DatabaseType databaseType);
}
