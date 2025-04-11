package academy.challenger.challenge;

public record ChallengeResponse (
        long id,
        long userId,
        String title,
        String description,
        Integer progress,
        Integer duration
) {
}
