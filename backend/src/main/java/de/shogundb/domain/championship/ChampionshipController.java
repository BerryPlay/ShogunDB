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
@RequestMapping(value = "/championship")
public class ChampionshipController {
    private final MemberRepository memberRepository;
    private final ChampionshipRepository championshipRepository;
    private final ChampionshipMemberRepository championshipMemberRepository;

    @Autowired
    public ChampionshipController(MemberRepository memberRepository, ChampionshipRepository championshipRepository, ChampionshipMemberRepository championshipMemberRepository) {
        this.memberRepository = memberRepository;
        this.championshipRepository = championshipRepository;
        this.championshipMemberRepository = championshipMemberRepository;
    }

    @GetMapping
    public ResponseEntity<Iterable<Championship>> index() {
        return ResponseEntity.ok(this.championshipRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Championship> store(@RequestBody @Valid ChampionshipRegisterDTO championship) throws MemberNotFoundException, ChampionshipNotFoundException {
        // setup members
        List<ChampionshipMember> championshipMembers = new ArrayList<>();
        for (ChampionshipMemberRegisterDTO championshipMemberRegisterDTO : championship.getMembers()) {
            championshipMembers.add(ChampionshipMember.builder()
                    .member(this.memberRepository
                            .findById(championshipMemberRegisterDTO.getMemberId())
                            .orElseThrow(()
                                    -> new MemberNotFoundException(championshipMemberRegisterDTO.getMemberId())))
                    .extra(championshipMemberRegisterDTO.getExtra())
                    .rank(championshipMemberRegisterDTO.getRank())
                    .build());
        }

        // build and save championship
        Championship newChampionship = this.championshipRepository.save(Championship.builder()
                .name(championship.getName())
                .date(championship.getDate())
                .build());

        // attach the members and championship
        for (ChampionshipMember championshipMember : championshipMembers) {
            championshipMember.setChampionship(newChampionship);
            championshipMember.getMember().getChampionships().add(championshipMember);
            newChampionship.getMembers().add(championshipMember);
        }

        this.championshipRepository.save(newChampionship);

        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentRequest().toUriString());
        return ResponseEntity.created(uri).body(this.championshipRepository
                .findById(newChampionship.getId())
                .orElseThrow(() -> new ChampionshipNotFoundException(newChampionship.getId())));
    }

    @PutMapping
    public ResponseEntity<Championship> update(@RequestBody @Valid ChampionshipUpdateDTO championship) throws ChampionshipNotFoundException, MemberNotFoundException {
        Championship oldChampionship = this.championshipRepository.findById(championship.getId())
                .orElseThrow(() -> new ChampionshipNotFoundException(championship.getId()));

        // update attributes
        oldChampionship.setName(championship.getName());
        oldChampionship.setDate(championship.getDate());

        // detach all old members
        List<ChampionshipMember> members = oldChampionship.getMembers();
        for (int i = 0; i < members.size(); i++) {
            ChampionshipMember member = members.get(i);
            member.getChampionship().getMembers().remove(member);
            member.getMember().getChampionships().remove(member);
            this.championshipMemberRepository.delete(member);
        }

        // attach the new members
        List<ChampionshipMember> championshipMembers = new ArrayList<>();
        for (ChampionshipMemberRegisterDTO championshipMember : championship.getMembers()) {
            Member member = this.memberRepository.findById(championshipMember.getMemberId())
                    .orElseThrow(() -> new MemberNotFoundException(championshipMember.getMemberId()));

            ChampionshipMember newChampionshipMember = ChampionshipMember.builder()
                    .member(member)
                    .championship(oldChampionship)
                    .extra(championshipMember.getExtra())
                    .rank(championshipMember.getRank())
                    .build();

            member.getChampionships().add(newChampionshipMember);
            oldChampionship.getMembers().add(newChampionshipMember);
        }

        oldChampionship = this.championshipRepository.save(oldChampionship);

        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentRequest().toUriString());
        return ResponseEntity.created(uri).body(oldChampionship);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) throws ChampionshipNotFoundException {
        Championship championship = this.championshipRepository.findById(id)
                .orElseThrow(() -> new ChampionshipNotFoundException(id));

        List<ChampionshipMember> members = championship.getMembers();
        for (int i = 0; i < members.size(); i++) {
            ChampionshipMember championshipMember = members.get(i);
            championshipMember.getMember().getChampionships().remove(championshipMember);
            championshipMember.getChampionship().getMembers().remove(championshipMember);
            this.championshipMemberRepository.delete(championshipMember);
        }

        this.championshipRepository.delete(championship);

        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Championship> show(@PathVariable Long id) throws ChampionshipNotFoundException {
        return ResponseEntity.ok(this.championshipRepository.findById(id)
                .orElseThrow(() -> new ChampionshipNotFoundException(id)));
    }
}
