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

    @Builder.Default
    private Boolean hasLeft = false;

    private LocalDate leftDate;

    @Builder.Default
    private Boolean isPassive = false;

    @ManyToOne
    @NotNull
    private Long contributionClass;

    @NotNull
    @Size(min = 1, max = 200)
    private String accountHolder;

    @Builder.Default
    @NotNull
    private List<Long> disciplines = new ArrayList<>();
}
