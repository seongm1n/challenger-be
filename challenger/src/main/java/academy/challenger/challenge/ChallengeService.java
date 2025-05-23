package academy.challenger.challenge;

import academy.challenger.exception.CustomException;
import academy.challenger.exception.ErrorCode;
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

    public ChallengeResponse save(ChallengeRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        LocalDate startDate = LocalDate.now();
        Challenge challenge = new Challenge(
                user,
                request.title(),
                request.description(),
                startDate,
                request.duration()
        );
        challengeRepository.save(challenge);
        return new ChallengeResponse(
                challenge.getId(),
                challenge.getUser().getId(),
                challenge.getTitle(),
                challenge.getDescription(),
                0.0,
                challenge.getDuration()
        );
    }

    public List<ChallengeResponse> getAllById(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        List<Challenge> challenges = challengeRepository.findAllByUser(user);
        return challenges.stream()
                .map(challenge -> new ChallengeResponse(
                        challenge.getId(),
                        challenge.getUser().getId(),
                        challenge.getTitle(),
                        challenge.getDescription(),
                        calculateProgress(challenge.getStartDate(), challenge.getDuration()),
                        challenge.getDuration()
                ))
                .toList();
    }

    public void delete(Long id) {
        Challenge challenge = challengeRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.CHALLENGE_NOT_FOUND));
        challengeRepository.delete(challenge);
    }

    private double calculateProgress(LocalDate startDate, int duration) {
        int elapsedDays = Period.between(startDate, LocalDate.now()).getDays() + 1;
        double progress = Math.min((double) elapsedDays / duration, 1.0);
        return Math.round(progress * 100) / 100.0;
    }
}
