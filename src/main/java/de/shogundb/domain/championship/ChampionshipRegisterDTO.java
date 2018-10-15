package de.shogundb.domain.championship;

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
@Builder
public class ChampionshipRegisterDTO {
    @NotNull
    @Size(min = 1, max = 200)
    private String name;

    @NotNull
    private Date date;

    @Builder.Default
    private List<ChampionshipMemberRegisterDTO> members = new ArrayList<>();
}
