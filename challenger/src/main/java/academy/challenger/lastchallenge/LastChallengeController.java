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
    public ResponseEntity<LastChallengeResponse> create(@RequestBody LastChallengeRequest request) {
        LastChallengeResponse response = lastChallengeService.save(request);
        log.info("LastChallenge created: {}", response);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<LastChallengeResponse>> getAllById(@PathVariable long id) {
        List<LastChallengeResponse> responses = lastChallengeService.getAllById(id);
        log.info("Retrieved all last challenges: {}", responses);
        return ResponseEntity.ok().body(responses);
    }
}
