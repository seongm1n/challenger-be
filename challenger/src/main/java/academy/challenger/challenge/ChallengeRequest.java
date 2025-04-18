package academy.challenger.challenge;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChallengeRequest(
        @NotNull(message = "유저 아이디가 없습니다.")
        long userId,
        @NotBlank(message = "챌린지 제목이 없습니다.")
        String title,
        @NotBlank(message = "챌린지 설명이 없습니다.")
        String description,
        @NotBlank(message = "챌린지 목표 일수가 없습니다.")
        Integer duration
) {
}
