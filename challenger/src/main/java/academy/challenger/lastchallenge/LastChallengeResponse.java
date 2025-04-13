package academy.challenger.lastchallenge;

public record LastChallengeResponse(
        long id,
        long userId,
        String title,
        String description,
        String duration,
        String retrospection,
        String assessment
) {
}
