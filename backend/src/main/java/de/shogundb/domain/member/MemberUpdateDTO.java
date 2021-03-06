package de.shogundb.domain.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
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
class MemberUpdateDTO {
    @NotNull
    private Long id;

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

    @NotBlank
    @Size(min = 1, max = 200)
    private String city;

    @NotNull
    @Size(min = 4, max = 200)
    private String phoneNumber;

    @Size(min = 4, max = 200)
    private String mobileNumber;

    @NotNull
    @Size(min = 4, max = 200)
    @Pattern(regexp = "^[A-Za-z0-9_.]+@[A-Za-z0-9_.]+\\.[A-Za-z0-9_.]+$")
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
    @NotNull
    private Long contributionClass;

    @NotNull
    @Size(min = 1, max = 200)
    private String accountHolder;

    @NotNull
    @Size(max = 1000)
    @Builder.Default
    private String notes = "";

    @Builder.Default
    private List<Long> disciplines = new ArrayList<>();

    @Builder.Default
    private List<Long> events = new ArrayList<>();

    @Builder.Default
    private List<Long> seminars = new ArrayList<>();
}
