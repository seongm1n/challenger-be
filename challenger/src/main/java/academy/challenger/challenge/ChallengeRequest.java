package academy.challenger.challenge;

public record ChallengeRequest(
        long userId,
        String title,
        String description,
        Integer duration
) {
}
