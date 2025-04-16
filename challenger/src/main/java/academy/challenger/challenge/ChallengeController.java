package academy.challenger.challenge;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/challenges")
@AllArgsConstructor
public class ChallengeController {
    private final ChallengeService challengeService;

    @PostMapping
    public ResponseEntity<ChallengeResponse> create(@Valid @RequestBody ChallengeRequest request) {
        ChallengeResponse response = challengeService.save(request);
        log.info("Challenge created: {}", response);
        return ResponseEntity.created(URI.create("/challenges/" + response.id())).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<ChallengeResponse>> getAllById(@PathVariable long id) {
        List<ChallengeResponse> responses = challengeService.getAllById(id);
        log.info("Retrieved all challenges: {}", responses);
        return ResponseEntity.ok().body(responses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        challengeService.delete(id);
        log.info("Challenge deleted with ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}
