package academy.challenger.lastchallenge;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LastChallengeRequest (
        @NotNull(message = "유저 아이디가 없습니다.")
        long userId,
        @NotNull(message = "챌린지 아이디가 없습니다.")
        long challengeId,
        @NotBlank(message = "회고가 없습니다.")
        String retrospection
){
}
