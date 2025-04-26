package academy.challenger.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ConfigurationProperties(prefix = "jwt")
@Component
public class JwtProperties {
    private String secretKey = "your_very_secure_secret_key_should_be_long_enough_for_security";
    private long expirationTime = 3600;
    private String tokenPrefix = "Bearer ";
    private String headerString = "Authorization";
}
