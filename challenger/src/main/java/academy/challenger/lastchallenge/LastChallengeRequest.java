package academy.challenger.lastchallenge;

public record LastChallengeRequest (
        long userId,
        long challengeId,
        String retrospection
){
}
