package academy.challenger.challenge;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("challenges")
@AllArgsConstructor
public class ChallengeController {
    private final ChallengeService challengeService;

    @PostMapping
    public ResponseEntity<ChallengeResponse> create(@RequestBody ChallengeRequest challengeRequest) {
        ChallengeResponse challengeResponse = challengeService.save(challengeRequest);
        return ResponseEntity.ok().body(challengeResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<ChallengeResponse>> getAllById(@PathVariable long id) {
        List<ChallengeResponse> challengeResponses = challengeService.getAllById(id);
        return ResponseEntity.ok().body(challengeResponses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        challengeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
