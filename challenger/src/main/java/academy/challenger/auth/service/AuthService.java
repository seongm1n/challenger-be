package academy.challenger.auth.service;

import academy.challenger.auth.dto.LoginRequest;
import academy.challenger.auth.dto.RegisterRequest;
import academy.challenger.auth.dto.TokenResponse;
import academy.challenger.auth.security.JwtTokenProvider;
import academy.challenger.auth.security.RefreshToken;
import academy.challenger.config.JwtProperties;
import academy.challenger.exception.CustomException;
import academy.challenger.exception.ErrorCode;
import academy.challenger.user.User;
import academy.challenger.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final JwtProperties jwtProperties;
    
    @Transactional
    public TokenResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        
        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }
        
        String accessToken = jwtTokenProvider.generateToken(user.getId());
        
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());
        
        return new TokenResponse(
                accessToken,
                refreshToken.getToken(),
                jwtProperties.getExpirationTime(),
                jwtProperties.getRefreshExpiration(),
                "Bearer"
        );
    }
    
    @Transactional
    public TokenResponse refreshToken(String refreshToken) {
        RefreshToken token = refreshTokenService.verifyExpiration(refreshToken);
        Long userId = token.getUserId();
        
        String accessToken = jwtTokenProvider.generateToken(userId);
        
        return new TokenResponse(
                accessToken,
                refreshToken,
                jwtProperties.getExpirationTime(),
                refreshTokenService.getExpiryTime(refreshToken),
                "Bearer"
        );
    }
    
    @Transactional
    public void logout(Long userId) {
        refreshTokenService.deleteByUserId(userId);
    }
    
    @Transactional
    public Long register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.email())) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        
        User user = User.builder()
                .email(registerRequest.email())
                .password(passwordEncoder.encode(registerRequest.password()))
                .name(registerRequest.name())
                .build();
        
        User savedUser = userRepository.save(user);
        return savedUser.getId();
    }
} 
