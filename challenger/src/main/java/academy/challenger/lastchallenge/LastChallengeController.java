package academy.challenger.lastchallenge;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/last-challenges")
@AllArgsConstructor
public class LastChallengeController {
    private final LastChallengeService lastChallengeService;

    @PostMapping
    public ResponseEntity<LastChallengeResponse> create(@RequestBody LastChallengeRequest lastChallengeRequest) {
        LastChallengeResponse lastChallengeResponse = lastChallengeService.save(lastChallengeRequest);
        log.info("LastChallenge created: {}", lastChallengeResponse);
        return ResponseEntity.ok().body(lastChallengeResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<LastChallengeResponse>> getAllById(@PathVariable long id) {
        List<LastChallengeResponse> lastChallengeResponses = lastChallengeService.getAllById(id);
        log.info("Retrieved all last challenges: {}", lastChallengeResponses);
        return ResponseEntity.ok().body(lastChallengeResponses);
    }
}
