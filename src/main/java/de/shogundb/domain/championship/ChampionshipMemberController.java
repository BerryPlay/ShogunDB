package de.shogundb.domain.championship;

import de.shogundb.domain.member.Member;
import de.shogundb.domain.member.MemberNotFoundException;
import de.shogundb.domain.member.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/championship/member")
public class ChampionshipMemberController {
    private final MemberRepository memberRepository;
    private final ChampionshipRepository championshipRepository;
    private final ChampionshipMemberRepository championshipMemberRepository;

    @Autowired
    public ChampionshipMemberController(MemberRepository memberRepository, ChampionshipRepository championshipRepository, ChampionshipMemberRepository championshipMemberRepository) {
        this.memberRepository = memberRepository;
        this.championshipRepository = championshipRepository;
        this.championshipMemberRepository = championshipMemberRepository;
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Iterable<ChampionshipMemberDTO>> index(@PathVariable Long id) throws ChampionshipNotFoundException {
        Championship championship = this.championshipRepository.findById(id)
                .orElseThrow(() -> new ChampionshipNotFoundException(id));

        List<ChampionshipMemberDTO> championshipMemberDTOList = new ArrayList<>();

        for (ChampionshipMember championshipMember : championship.getMembers()) {
            championshipMemberDTOList.add(ChampionshipMemberDTO.builder()
                    .id(championshipMember.getId())
                    .member(championshipMember.getMember())
                    .rank(championshipMember.getRank())
                    .extra(championshipMember.getExtra())
                    .build());
        }

        return ResponseEntity.ok(championshipMemberDTOList);
    }

    @PostMapping(value = "/{id}")
    public ResponseEntity<ChampionshipMember> store(
            @PathVariable Long id,
            @RequestBody @Valid ChampionshipMemberRegisterDTO championshipMemberRegisterDTO)
            throws ChampionshipNotFoundException, MemberNotFoundException {
        Championship championship = this.championshipRepository.findById(id).
                orElseThrow(() -> new ChampionshipNotFoundException(id));
        Member member = this.memberRepository.findById(championshipMemberRegisterDTO.getMemberId())
                .orElseThrow(() -> new MemberNotFoundException(championshipMemberRegisterDTO.getMemberId()));

        // check, if the member is already part of the championship
        for (ChampionshipMember championshipMember : championship.getMembers()) {
            if (championshipMember.getMember().equals(member)) {
                return ResponseEntity.status(409).build();
            }
        }

        ChampionshipMember championshipMember = ChampionshipMember.builder()
                .member(member)
                .championship(championship)
                .rank(championshipMemberRegisterDTO.getRank())
                .extra(championshipMemberRegisterDTO.getExtra())
                .build();

        member.getChampionships().add(championshipMember);
        championship.getMembers().add(championshipMember);
        championshipMember = this.championshipMemberRepository.save(championshipMember);

        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentRequest().toUriString());
        return ResponseEntity.created(uri).body(championshipMember);
    }

    @PutMapping
    public ResponseEntity<ChampionshipMember> update(@RequestBody @Valid ChampionshipMemberDTO championshipMemberDTO) throws ChampionshipMemberNotFoundException {
        ChampionshipMember championshipMember = this.championshipMemberRepository.findById(championshipMemberDTO.getId())
                .orElseThrow(() -> new ChampionshipMemberNotFoundException(championshipMemberDTO.getId()));

        championshipMember.setRank(championshipMemberDTO.getRank());
        championshipMember.setExtra(championshipMemberDTO.getExtra());

        championshipMember = this.championshipMemberRepository.save(championshipMember);

        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentRequest().toUriString());
        return ResponseEntity.created(uri).body(championshipMember);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) throws ChampionshipMemberNotFoundException {
        ChampionshipMember championshipMember = this.championshipMemberRepository.findById(id)
                .orElseThrow(() -> new ChampionshipMemberNotFoundException(id));

        championshipMember.getMember().getChampionships().remove(championshipMember);
        championshipMember.getChampionship().getMembers().remove(championshipMember);

        this.championshipMemberRepository.save(championshipMember);
        this.championshipMemberRepository.delete(id);

        return ResponseEntity.noContent().build();
    }
}
