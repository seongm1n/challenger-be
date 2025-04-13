package academy.challenger.lastchallenge;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("last-challenges")
@AllArgsConstructor
public class LastChallengeController {
    private final LastChallengeService lastChallengeService;

    @PostMapping
    public ResponseEntity<LastChallengeResponse> create(@RequestBody LastChallengeRequest lastChallengeRequest) {
        LastChallengeResponse lastChallengeResponse = lastChallengeService.save(lastChallengeRequest);
        return ResponseEntity.ok().body(lastChallengeResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<LastChallengeResponse>> getAllById(@PathVariable long id) {
        List<LastChallengeResponse> lastChallengeResponses = lastChallengeService.getAllById(id);
        return ResponseEntity.ok().body(lastChallengeResponses);
    }
}
