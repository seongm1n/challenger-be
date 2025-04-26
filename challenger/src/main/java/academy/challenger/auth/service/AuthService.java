package academy.challenger.auth.service;

import academy.challenger.auth.dto.LoginRequest;
import academy.challenger.auth.dto.RegisterRequest;
import academy.challenger.auth.security.JwtTokenProvider;
import academy.challenger.exception.CustomException;
import academy.challenger.exception.ErrorCode;
import academy.challenger.user.User;
import academy.challenger.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    
    @Transactional
    public String login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
                
        if (!user.matchPassword(loginRequest.password())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }
        
        return jwtTokenProvider.generateToken(user.getId());
    }
    
    @Transactional
    public Long register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.email())) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        
        User user = User.builder()
                .email(registerRequest.email())
                .password(registerRequest.password()) // 실제로는 암호화 필요
                .name(registerRequest.name())
                .build();
        
        User savedUser = userRepository.save(user);
        return savedUser.getId();
    }
} 