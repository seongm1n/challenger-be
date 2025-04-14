package academy.challenger.lastchallenge;

import java.time.LocalDate;

public record LastChallengeResponse(
        long id,
        long userId,
        String title,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        String retrospection,
        String assessment
) {
}
