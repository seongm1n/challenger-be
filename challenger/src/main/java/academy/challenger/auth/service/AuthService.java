package academy.challenger.auth.service;

import academy.challenger.auth.dto.LoginRequest;
import academy.challenger.auth.dto.RegisterRequest;
import academy.challenger.auth.security.JwtTokenProvider;
import academy.challenger.exception.CustomException;
import academy.challenger.exception.ErrorCode;
import academy.challenger.user.User;
import academy.challenger.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    
    @Transactional
    public String login(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password())
            );
            
            User user = userRepository.findByEmail(loginRequest.email())
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
                    
            return jwtTokenProvider.generateToken(user.getId());
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }
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