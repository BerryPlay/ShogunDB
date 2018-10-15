package de.shogundb.domain.seminar;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
class SeminarRegisterDTO {
    @NotNull
    @Size(min = 1, max = 200)
    private String name;

    @NotNull
    @Size(min = 1, max = 200)
    private String place;

    @NotNull
    private Date dateFrom;

    @NotNull
    private Date dateTo;

    @NotNull
    private SeminarType seminarType;

    @Builder.Default
    private List<Long> referents = new ArrayList<>();

    @Builder.Default
    private List<Long> members = new ArrayList<>();
}
