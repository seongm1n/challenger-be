package academy.challenger.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secretKey;
    private long expirationTime;
    private long refreshExpiration;
    private String tokenPrefix;
    private String headerString;
}
