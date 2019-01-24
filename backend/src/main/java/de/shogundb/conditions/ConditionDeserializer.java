package de.shogundb.conditions;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.shogundb.conditions.statements.*;
import de.shogundb.domain.seminar.SeminarType;
import org.apache.commons.lang3.EnumUtils;

import java.io.IOException;

/**
 * A custom deserializer for the condition interface and all classes which implements.
 */
public class ConditionDeserializer extends JsonDeserializer<Condition> {
    @Override
    public Condition deserialize(JsonParser parser, DeserializationContext context)
            throws IOException {
        var mapper = (ObjectMapper) parser.getCodec();
        var root = (ObjectNode) mapper.readTree(parser);

        if (root.has("type")) {
            // Switch the condition type value
            switch (ConditionType.valueOf(root.get("type").asText())) {
                case AGE:
                    if (root.get("minAge").asInt() <= root.get("maxAge").asInt()) {
                        return mapper.readValue(root.toString(), AgeCondition.class);
                    }
                    break;
                case AND:
                    if (root.get("conditions").size() > 0) {
                        return mapper.readValue(root.toString(), AndCondition.class);
                    }
                    break;
                case GRADUATION:
                    if (root.get("id").asLong() > 0L
                            && root.get("period").asInt() > 0
                            && EnumUtils.isValidEnum(PeriodFormat.class, root.get("periodFormat").asText())) {
                        return mapper.readValue(root.toString(), GraduationCondition.class);
                    }
                    break;
                case MEMBERSHIP:
                    if (root.get("period").asInt() > 0
                            && EnumUtils.isValidEnum(PeriodFormat.class, root.get("periodFormat").asText())) {
                        return mapper.readValue(root.toString(), MembershipCondition.class);
                    }
                    break;
                case OR:
                    if (root.get("conditions").size() > 0) {
                        return mapper.readValue(root.toString(), OrCondition.class);
                    }
                    break;
                case SEMINAR:
                    if (root.get("quantity").asInt() > 0
                            && root.get("period").asInt() > 0
                            && EnumUtils.isValidEnum(PeriodFormat.class, root.get("periodFormat").asText())
                            && EnumUtils.isValidEnum(SeminarType.class, root.get("seminarType").asText())) {
                        return mapper.readValue(root.toString(), SeminarCondition.class);
                    }
                    break;
            }
        }
        return null;
    }
}
