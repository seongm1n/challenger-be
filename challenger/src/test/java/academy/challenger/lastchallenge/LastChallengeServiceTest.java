package academy.challenger.lastchallenge;

import academy.challenger.challenge.Challenge;
import academy.challenger.challenge.ChallengeRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LastChallengeServiceTest {

    @Mock
    private LastChallengeRepository lastChallengeRepository;

    @Mock
    private ChallengeRepository challengeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AIReviewService aiReviewService;

    @InjectMocks
    private LastChallengeService lastChallengeService;

    private User user;
    private Challenge challenge;
    private LastChallenge lastChallenge;
    private LastChallengeRequest lastChallengeRequest;

    @BeforeEach
    void setUp() {
        user = new User(1L, "테스트유저");
        challenge = new Challenge(user, "테스트 챌린지", "테스트 설명", LocalDate.of(2025, 4, 9), 30);
        lastChallenge = new LastChallenge(user, "테스트 챌린지", "테스트 설명", LocalDate.of(2025, 4, 10), LocalDate.of(2025, 4, 15), "테스트 회고", "테스트 리뷰");
        lastChallengeRequest = new LastChallengeRequest(1L, 1L, "테스트 회고");

        try {
            java.lang.reflect.Field idField = Challenge.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(challenge, 1L);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            java.lang.reflect.Field idField = LastChallenge.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(lastChallenge, 1L);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void 지난_챌린지_저장_성공() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(challengeRepository.findById(1L)).thenReturn(Optional.of(challenge));
        when(aiReviewService.getReview("테스트 회고", user.getName())).thenReturn("테스트 리뷰");
        when(lastChallengeRepository.save(any(LastChallenge.class))).thenAnswer(invocation -> {
            LastChallenge savedChallenge = invocation.getArgument(0);
            try {
                java.lang.reflect.Field idField = LastChallenge.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(savedChallenge, 1L);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return savedChallenge;
        });

        // when
        LastChallengeResponse response = lastChallengeService.save(lastChallengeRequest);

        // then
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.userId()).isEqualTo(1L);
        assertThat(response.title()).isEqualTo("테스트 챌린지");
        assertThat(response.description()).isEqualTo("테스트 설명");
        assertThat(response.startDate()).isEqualTo(challenge.getStartDate());
        assertThat(response.endDate()).isEqualTo(LocalDate.now());
        assertThat(response.retrospection()).isEqualTo("테스트 회고");
        assertThat(response.assessment()).isEqualTo("테스트 리뷰");

        verify(userRepository).findById(1L);
        verify(challengeRepository).findById(1L);
        verify(lastChallengeRepository).save(any(LastChallenge.class));
        verify(challengeRepository).delete(challenge);
    }

    @Test
    void 지난_챌린지_전체조회() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(lastChallengeRepository.findAllByUserOrderByIdDesc(user)).thenReturn(List.of(lastChallenge));

        // when
        List<LastChallengeResponse> responses = lastChallengeService.getAllById(1L);

        // then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).id()).isEqualTo(1L);
        assertThat(responses.get(0).userId()).isEqualTo(1L);
        assertThat(responses.get(0).title()).isEqualTo("테스트 챌린지");
        assertThat(responses.get(0).description()).isEqualTo("테스트 설명");
        assertThat(responses.get(0).startDate()).isEqualTo(lastChallenge.getStartDate());
        assertThat(responses.get(0).endDate()).isEqualTo(lastChallenge.getEndDate());
        assertThat(responses.get(0).retrospection()).isEqualTo("테스트 회고");
        assertThat(responses.get(0).assessment()).isEqualTo("테스트 리뷰");

        verify(userRepository).findById(1L);
        verify(lastChallengeRepository).findAllByUserOrderByIdDesc(user);
    }
}
