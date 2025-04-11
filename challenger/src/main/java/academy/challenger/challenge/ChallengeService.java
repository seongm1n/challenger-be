package academy.challenger.challenge;

import academy.challenger.user.User;
import academy.challenger.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
@AllArgsConstructor
public class ChallengeService {
    private final ChallengeRepository challengeRepository;
    private final UserRepository userRepository;

    public ChallengeResponse save(ChallengeRequest challengeRequest) {
        User user = userRepository.findById(challengeRequest.userId());
        LocalDate startDate = LocalDate.now();
        Challenge challenge = new Challenge(
                user,
                challengeRequest.title(),
                challengeRequest.description(),
                startDate,
                challengeRequest.duration()
        );
        challengeRepository.save(challenge);
        return new ChallengeResponse(
                challenge.getId(),
                challenge.getUser().getId(),
                challenge.getTitle(),
                challenge.getDescription(),
                1,
                challenge.getDuration()
        );
    }

    public List<ChallengeResponse> getAllById(long id) {
        User user = userRepository.findById(id);
        List<Challenge> challenges = challengeRepository.findAllByUser(user);
        return challenges.stream()
                .map(challenge -> new ChallengeResponse(
                        challenge.getId(),
                        challenge.getUser().getId(),
                        challenge.getTitle(),
                        challenge.getDescription(),
                        Period.between(challenge.getStartDate(), LocalDate.now()).getDays() + 1,
                        challenge.getDuration()
                ))
                .toList();
    }

    public void delete(Long id) {
        Challenge challenge = challengeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 Challenge를 찾을 수 없습니다: " + id));
        challengeRepository.delete(challenge);
    }
}
