package academy.challenger.challenge;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/challenges")
@AllArgsConstructor
public class ChallengeController {
    private final ChallengeService challengeService;

    @PostMapping
    public ResponseEntity<ChallengeResponse> create(@RequestBody ChallengeRequest challengeRequest) {
        ChallengeResponse challengeResponse = challengeService.save(challengeRequest);
        return ResponseEntity.created(URI.create("/challenges/" + challengeResponse.id())).body(challengeResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<ChallengeResponse>> getAllById(@PathVariable long id) {
        List<ChallengeResponse> challengeResponses = challengeService.getAllById(id);
        log.info("Retrieved all challenges: {}", challengeResponses);
        return ResponseEntity.ok().body(challengeResponses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        challengeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
