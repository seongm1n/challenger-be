package academy.challenger.lastchallenge;

import jakarta.validation.constraints.NotBlank;

public record LastChallengeRequest (
        @NotBlank(message = "유저 아이디가 없습니다.")
        long userId,
        @NotBlank(message = "챌린지 아이디가 없습니다.")
        long challengeId,
        @NotBlank(message = "회고가 없습니다.")
        String retrospection
){
}
