package de.shogundb.domain.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
class MemberRegisterDTO {
    @NotNull
    @Size(min = 1, max = 200)
    private String forename;

    @NotNull
    @Size(min = 1, max = 200)
    private String surname;

    @NotNull
    private Gender gender;

    @NotNull
    @Size(min = 1, max = 200)
    private String street;

    @NotNull
    @Size(min = 3, max = 8)
    private String postcode;

    @NotNull
    @Size(min = 4, max = 200)
    private String phoneNumber;

    @Size(min = 4, max = 200)
    private String mobileNumber;

    @NotNull
    @Size(min = 4, max = 200)
    @Pattern(regexp = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*" +
            "|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01" +
            "-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a" +
            "-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\" +
            ".){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x0" +
            "8\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-" +
            "\\x7f])+)])")
    private String email;

    @NotNull
    private LocalDate dateOfBirth;

    @NotNull
    private Boolean hasBudoPass;

    private LocalDate budoPassDate;

    @NotNull
    private LocalDate enteredDate;

    @NotNull
    private Boolean hasLeft;

    private LocalDate leftDate;

    @NotNull
    private Boolean isPassive;

    @ManyToOne
    private Long contributionClass;

    @NotNull
    @Size(min = 1, max = 200)
    private String accountHolder;

    @Builder.Default
    private List<Long> disciplines = new ArrayList<>();
}
