package academy.challenger.config;

import academy.challenger.user.User;
import academy.challenger.user.UserRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestConfiguration
@EnableWebSecurity
public class TestSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            );
        
        return http.build();
    }

    @Bean
    @Primary
    public UserRepository mockUserRepository() {
        UserRepository mockRepository = mock(UserRepository.class);
        
        User testUser = User.builder()
            .id(1L)
            .name("테스트사용자")
            .email("test@example.com")
            .password("password")
            .build();
        
        when(mockRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        
        return mockRepository;
    }
} 