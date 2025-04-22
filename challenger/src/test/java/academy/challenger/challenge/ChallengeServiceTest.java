package academy.challenger.challenge;

import academy.challenger.exception.CustomException;
import academy.challenger.exception.ErrorCode;
import academy.challenger.user.User;
import academy.challenger.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChallengeServiceTest {

    @Mock
    private ChallengeRepository challengeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ChallengeService challengeService;

    private User user;
    private Challenge challenge;
    private ChallengeRequest challengeRequest;
    private LocalDate now;

    @BeforeEach
    void setUp() {
        now = LocalDate.now();
        user = new User(1L, "테스트사용자");
        challengeRequest = new ChallengeRequest(1L, "테스트 챌린지", "테스트 설명", 30);
        challenge = new Challenge(user, "테스트 챌린지", "테스트 설명", now, 30);
        
        // challenge의 id 설정
        // 리플렉션을 사용하여 private 필드에 접근
        try {
            java.lang.reflect.Field idField = Challenge.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(challenge, 1L);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void 챌린지_저장_성공() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(challengeRepository.save(any(Challenge.class))).thenAnswer(invocation -> {
            Challenge savedChallenge = invocation.getArgument(0);
            try {
                java.lang.reflect.Field idField = Challenge.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(savedChallenge, 1L);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return savedChallenge;
        });

        // when
        ChallengeResponse response = challengeService.save(challengeRequest);

        // then
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.userId()).isEqualTo(1L);
        assertThat(response.title()).isEqualTo("테스트 챌린지");
        assertThat(response.description()).isEqualTo("테스트 설명");
        assertThat(response.progress()).isEqualTo(0.0);
        assertThat(response.duration()).isEqualTo(30);
        
        verify(userRepository).findById(1L);
        verify(challengeRepository).save(any(Challenge.class));
    }

    @Test
    void 챌린지_저장_실패_사용자없음() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> {
            challengeService.save(challengeRequest);
        });
        
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
        verify(userRepository).findById(1L);
        verify(challengeRepository, never()).save(any(Challenge.class));
    }

    @Test
    void 챌린지_전체조회_성공() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(challengeRepository.findAllByUser(user)).thenReturn(List.of(challenge));

        // when
        List<ChallengeResponse> responses = challengeService.getAllById(1L);

        // then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).id()).isEqualTo(1L);
        assertThat(responses.get(0).userId()).isEqualTo(1L);
        assertThat(responses.get(0).title()).isEqualTo("테스트 챌린지");
        assertThat(responses.get(0).description()).isEqualTo("테스트 설명");
        assertThat(responses.get(0).duration()).isEqualTo(30);
        
        verify(userRepository).findById(1L);
        verify(challengeRepository).findAllByUser(user);
    }

    @Test
    void 챌린지_전체조회_실패_사용자없음() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> {
            challengeService.getAllById(1L);
        });
        
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
        verify(userRepository).findById(1L);
        verify(challengeRepository, never()).findAllByUser(any(User.class));
    }

    @Test
    void 챌린지_삭제_성공() {
        // given
        when(challengeRepository.findById(1L)).thenReturn(Optional.of(challenge));
        doNothing().when(challengeRepository).delete(challenge);

        // when
        challengeService.delete(1L);

        // then
        verify(challengeRepository).findById(1L);
        verify(challengeRepository).delete(challenge);
    }

    @Test
    void 챌린지_삭제_실패_챌린지없음() {
        // given
        when(challengeRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> {
            challengeService.delete(1L);
        });
        
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.CHALLENGE_NOT_FOUND);
        verify(challengeRepository).findById(1L);
        verify(challengeRepository, never()).delete(any(Challenge.class));
    }
}
