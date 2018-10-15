package de.shogundb.domain.championship;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChampionshipMemberRegisterDTO {
    @NotNull
    private Long memberId;

    private int rank;

    private String extra;
}
