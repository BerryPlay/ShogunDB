package de.shogundb;

import com.google.gson.*;
import de.shogundb.domain.contributionClass.ContributionClass;
import de.shogundb.domain.contributionClass.ContributionClassRepository;
import de.shogundb.domain.member.Gender;
import de.shogundb.domain.member.Member;
import de.shogundb.domain.person.Person;
import de.shogundb.domain.seminar.Seminar;
import de.shogundb.domain.seminar.SeminarType;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
                .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (date, type, jsonSerializationContext)
                        -> new JsonPrimitive(date.toString()))
                .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (date, type, jsonSerializationContext)
                        -> new JsonPrimitive(date.toString()))
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
                .dateOfBirth(LocalDate.parse("2018-01-02"))
                .hasBudoPass(true)
                .budoPassDate(LocalDate.parse("2018-01-02"))
                .enteredDate(LocalDate.parse("2018-01-02"))
                .hasLeft(true)
                .leftDate(LocalDate.parse("2018-01-03"))
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
                .dateTo(LocalDate.parse("2018-01-02"))
                .dateFrom(LocalDate.parse("2018-01-02"))
                .build();
    }
}
