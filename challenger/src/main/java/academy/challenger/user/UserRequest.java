package academy.challenger.user;

import jakarta.validation.constraints.NotBlank;

public record UserRequest(
        @NotBlank(message = "유저 닉네임이 없습니다.")
        String username
) {
}
