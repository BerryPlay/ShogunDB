package de.shogundb.conditions;

import de.shogundb.domain.member.Member;
import de.shogundb.domain.member.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/con")
public class ConditionController {
    private final MemberRepository memberRepository;

    @PersistenceContext
    private EntityManager em;

    @Value("${spring.jpa.database}")
    private String databaseType;

    @Autowired
    public ConditionController(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /**
     * An endpoint to test the condition functionality.
     *
     * @param condition all conditions
     * @return a HTTP OK with a list of all members matching the conditions
     */
    @PostMapping
    public ResponseEntity<List<Member>> test(@RequestBody @Valid MainCondition condition) {
        var query = em.createNativeQuery(condition.getSQLQuery(DatabaseType.valueOf(databaseType)), Member.class);

        System.out.println(condition.getSQLQuery(DatabaseType.valueOf(databaseType)));
        var res = query.getResultList();

        return ResponseEntity.ok().body(new ArrayList<>() {{
            for (var member : res) {
                add((Member) member);
            }
        }});
    }
}
