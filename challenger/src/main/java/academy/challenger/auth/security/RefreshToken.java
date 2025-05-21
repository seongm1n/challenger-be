package academy.challenger.auth.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
    private String id;
    private Long userId;
    private String token;
    private Date expiryDate;
}
