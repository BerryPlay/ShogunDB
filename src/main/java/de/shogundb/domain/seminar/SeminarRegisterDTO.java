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

/**
 * A data transfer object for adding a new seminar
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
class SeminarRegisterDTO {
    /**
     * Name of the new seminar.
     * Can't be null and must have a minimum of one and a maximum of 200 character.
     */
    @NotNull
    @Size(min = 1, max = 200)
    private String name;

    /**
     * Place of the new seminar.
     * Can't be null and must have a minimum of one and a maximum of 200 character.
     */
    @NotNull
    @Size(min = 1, max = 200)
    private String place;

    /**
     * Date of the new seminar, when the seminar starts.
     * Can't be null.
     */
    @NotNull
    private Date dateFrom;

    /**
     * Date of the new seminar, when the seminar ends. If the seminar is only one day long, the dateTo is the same as
     * the dateFrom.
     * Can't be null.
     */
    @NotNull
    private Date dateTo;

    /**
     * Type of the seminar. Can be LOCAL, REGIONAL, NATION or GLOBAL.
     * Can't be null.
     */
    @NotNull
    private SeminarType seminarType;

    /**
     * A list of ids (of persons) of all referents of the seminar.
     * Can't be null.
     */
    @Builder.Default
    private List<Long> referents = new ArrayList<>();

    /**
     * A list of ids of all members of the seminar.
     * Can't be null.
     */
    @Builder.Default
    private List<Long> members = new ArrayList<>();
}
