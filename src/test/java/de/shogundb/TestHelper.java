package de.shogundb;

import com.google.gson.*;
import de.shogundb.domain.contributionClass.ContributionClass;
import de.shogundb.domain.contributionClass.ContributionClassRepository;
import de.shogundb.domain.member.Gender;
import de.shogundb.domain.member.Member;
import de.shogundb.domain.person.Person;
import de.shogundb.domain.seminar.Seminar;
import de.shogundb.domain.seminar.SeminarType;

import java.util.Date;

public class TestHelper {
    /**
     * Converts the given object to a json string. Automatically converts date objects to long values.
     *
     * @param o object to convert to json
     * @return the json string of the given object
     */
    public static String toJson(Object o) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json, typeOfT, context)
                        -> new Date(json.getAsJsonPrimitive().getAsLong()))
                .registerTypeAdapter(Date.class, (JsonSerializer<Date>) (date, type, jsonSerializationContext)
                        -> new JsonPrimitive(date.getTime()))
                .create();

        return gson.toJson(o);
    }

    /**
     * Creates a member and adds a new contribution class to the database.
     *
     * @param contributionClassRepository repository to get, store, update and delete contribution classes
     * @return a new member
     */
    public static Member createTestMember(ContributionClassRepository contributionClassRepository) {
        return Member.builder()
                .forename("Max")
                .surname("Mustermann")
                .gender(Gender.MALE)
                .street("Musterstraße")
                .postcode("26721")
                .phoneNumber("04929 5435438")
                .mobileNumber("1522 416845575")
                .email("max@muster.de")
                .dateOfBirth(new Date(810086400000L))
                .hasBudoPass(true)
                .budoPassDate(new Date(1514764800000L))
                .enteredDate(new Date(1514764800000L))
                .hasLeft(false)
                .leftDate(null)
                .isPassive(false)
                .contributionClass(
                        contributionClassRepository.save(
                                ContributionClass.builder()
                                        .name("Test")
                                        .baseContribution(27.7)
                                        .additionalContribution(5)
                                        .build()))
                .accountHolder("Max Mustermann")
                .build();
    }

    /**
     * Creates a new person.
     *
     * @return a new person.
     */
    public static Person createTestPerson() {
        return Person.builder()
                .name("Test Person")
                .build();
    }

    /**
     * Creates a new seminar.
     *
     * @return a new seminar
     */
    public static Seminar createTestSeminar() {
        return Seminar.builder()
                .name("Test Seminar")
                .place("Test Place")
                .seminarType(SeminarType.NATIONAL)
                .dateTo(new Date(1515283200000L))
                .dateFrom(new Date(1515196800000L))
                .build();
    }
}
