package academy.challenger.lastchallenge;

import academy.challenger.challenge.Challenge;
import academy.challenger.challenge.ChallengeRepository;
import academy.challenger.user.User;
import academy.challenger.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class LastChallengeService {
    private final LastChallengeRepository lastChallengeRepository;
    private final ChallengeRepository challengeRepository;
    private final UserRepository userRepository;
    private final AIReviewService aiReviewService;

    @Transactional
    public LastChallengeResponse save(LastChallengeRequest lastChallengeRequest) {
        User user = userRepository.findById(lastChallengeRequest.userId())
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 User를 찾을 수 없습니다: " + lastChallengeRequest.userId()));
        Challenge challenge = challengeRepository.findById(lastChallengeRequest.challengeId())
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 Challenge를 찾을 수 없습니다: " + lastChallengeRequest.challengeId()));

        String review = aiReviewService.getReview(
                lastChallengeRequest.retrospection(),
                user.getName()
        );

        LastChallenge lastChallenge = new LastChallenge(
                user,
                challenge.getTitle(),
                challenge.getDescription(),
                challenge.getStartDate(),
                LocalDate.now(),
                lastChallengeRequest.retrospection(),
                review
        );

        lastChallengeRepository.save(lastChallenge);
        challengeRepository.delete(challenge);
        return new LastChallengeResponse(
                lastChallenge.getId(),
                lastChallenge.getUser().getId(),
                lastChallenge.getTitle(),
                lastChallenge.getDescription(),
                lastChallenge.getStartDate() + " - " + lastChallenge.getEndDate(),
                lastChallenge.getRetrospection(),
                lastChallenge.getAssessment()
        );
    }

    public List<LastChallengeResponse> getAllById(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 User를 찾을 수 없습니다: " + id));
        List<LastChallenge> lastChallenges = lastChallengeRepository.findAllByUser(user);
        return lastChallenges.stream()
                .map(lastChallenge -> new LastChallengeResponse(
                        lastChallenge.getId(),
                        lastChallenge.getUser().getId(),
                        lastChallenge.getTitle(),
                        lastChallenge.getDescription(),
                        lastChallenge.getStartDate() + " - " + lastChallenge.getEndDate(),
                        lastChallenge.getRetrospection(),
                        lastChallenge.getAssessment()
                ))
                .toList();
    }
}
