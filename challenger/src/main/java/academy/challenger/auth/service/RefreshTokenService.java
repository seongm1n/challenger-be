package academy.challenger.auth.service;

import academy.challenger.auth.security.JwtTokenProvider;
import academy.challenger.auth.security.RefreshToken;
import academy.challenger.config.JwtProperties;
import academy.challenger.exception.CustomException;
import academy.challenger.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    
    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";
    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtProperties jwtProperties;
    private final JwtTokenProvider jwtTokenProvider;

    public RefreshToken createRefreshToken(Long userId) {
        deleteByUserId(userId);
        
        String tokenId = UUID.randomUUID().toString();
        String token = jwtTokenProvider.generateRefreshToken();
        Date expiryDate = new Date(System.currentTimeMillis() + jwtProperties.getRefreshExpiration());
        
        RefreshToken refreshToken = RefreshToken.builder()
                .id(tokenId)
                .userId(userId)
                .token(token)
                .expiryDate(expiryDate)
                .build();
        
        redisTemplate.opsForValue().set(
                REFRESH_TOKEN_PREFIX + token,
                String.valueOf(userId),
                jwtProperties.getRefreshExpiration(),
                TimeUnit.MILLISECONDS
        );
        
        redisTemplate.opsForValue().set(
                REFRESH_TOKEN_PREFIX + "user:" + userId,
                token,
                jwtProperties.getRefreshExpiration(),
                TimeUnit.MILLISECONDS
        );
        
        return refreshToken;
    }
    
    public Optional<Long> findUserIdByToken(String token) {
        String userIdStr = (String) redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + token);
        if (userIdStr != null) {
            return Optional.of(Long.parseLong(userIdStr));
        }
        return Optional.empty();
    }
    
    public void deleteByUserId(Long userId) {
        String token = (String) redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + "user:" + userId);
        if (token != null) {
            redisTemplate.delete(REFRESH_TOKEN_PREFIX + token);
            redisTemplate.delete(REFRESH_TOKEN_PREFIX + "user:" + userId);
        }
    }
    
    public void deleteByToken(String token) {
        String userIdStr = (String) redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + token);
        if (userIdStr != null) {
            Long userId = Long.parseLong(userIdStr);
            redisTemplate.delete(REFRESH_TOKEN_PREFIX + token);
            redisTemplate.delete(REFRESH_TOKEN_PREFIX + "user:" + userId);
        }
    }
    
    public RefreshToken verifyExpiration(String token) {
        String userIdStr = (String) redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + token);
        if (userIdStr == null) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
        
        Long userId = Long.parseLong(userIdStr);
        Long ttl = redisTemplate.getExpire(REFRESH_TOKEN_PREFIX + token, TimeUnit.MILLISECONDS);
        
        if (ttl == null || ttl <= 0) {
            deleteByToken(token);
            throw new CustomException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        }
        
        String tokenId = UUID.randomUUID().toString();
        Date expiryDate = new Date(System.currentTimeMillis() + ttl);
        
        return RefreshToken.builder()
                .id(tokenId)
                .userId(userId)
                .token(token)
                .expiryDate(expiryDate)
                .build();
    }
    
    public long getExpiryTime(String token) {
        Long ttl = redisTemplate.getExpire(REFRESH_TOKEN_PREFIX + token, TimeUnit.MILLISECONDS);
        return ttl != null && ttl > 0 ? ttl : 0;
    }
}
