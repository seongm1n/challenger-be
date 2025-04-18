package academy.challenger.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void 사용자_저장_신규사용자() {
        // given
        String username = "신규사용자";
        UserRequest request = new UserRequest(username);
        User savedUser = new User(1L, username);

        when(userRepository.findByName(username)).thenReturn(null);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // when
        UserResponse response = userService.save(request);

        // then
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.username()).isEqualTo(username);
        verify(userRepository).findByName(username);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void 사용자_저장_기존사용자() {
        // given
        String username = "기존사용자";
        UserRequest request = new UserRequest(username);
        User existingUser = new User(1L, username);
        
        when(userRepository.findByName(username)).thenReturn(existingUser);

        // when
        UserResponse response = userService.save(request);

        // then
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.username()).isEqualTo(username);
        verify(userRepository).findByName(username);
        verify(userRepository, never()).save(any(User.class));
    }
}
