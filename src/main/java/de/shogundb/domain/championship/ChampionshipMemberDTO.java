package de.shogundb.domain.championship;

import de.shogundb.domain.member.Member;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode

class ChampionshipMemberDTO {
    private Long id;
    private Member member;
    private int rank;
    private String extra;
}
