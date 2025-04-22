package academy.challenger.lastchallenge;

import academy.challenger.user.User;
import academy.challenger.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class LastChallengeRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private LastChallengeRepository lastChallengeRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(null, "테스트유저");
        entityManager.persist(user);

        LastChallenge challenge1 = createLastChallenge(
                "첫번째 챌린지",
                "첫번째 설명",
                LocalDate.of(2025, 3, 1),
                LocalDate.of(2025, 3, 10),
                user
        );

        LastChallenge challenge2 = createLastChallenge(
                "두번째 챌린지",
                "두번째 설명",
                LocalDate.of(2025, 4, 1),
                LocalDate.of(2025, 4, 10),
                user
        );

        LastChallenge challenge3 = createLastChallenge(
                "세번째 챌린지",
                "세번째 설명",
                LocalDate.of(2025, 5, 1),
                LocalDate.of(2025, 5, 10),
                user
        );

        entityManager.persist(challenge1);
        entityManager.persist(challenge2);
        entityManager.persist(challenge3);

        entityManager.flush();
    }

    private LastChallenge createLastChallenge(String title, String description,
                                              LocalDate startDate, LocalDate endDate,
                                              User user) {
        return new LastChallenge(
                user,
                title,
                description,
                startDate,
                endDate,
                title + " 회고",
                title + " 리뷰"
        );
    }

    @Test
    @DisplayName("유저별 지난 챌린지 ID 내림차순 조회 테스트")
    void findAllByUserOrderByIdDesc_ShouldReturnChallengesInDescOrder() {
        // when
        List<LastChallenge> challenges = lastChallengeRepository.findAllByUserOrderByIdDesc(user);

        // then
        assertThat(challenges).hasSize(3);

        assertThat(challenges.get(0).getId()).isGreaterThan(challenges.get(1).getId());
        assertThat(challenges.get(1).getId()).isGreaterThan(challenges.get(2).getId());

        assertThat(challenges.get(0).getTitle()).isEqualTo("세번째 챌린지");
        assertThat(challenges.get(1).getTitle()).isEqualTo("두번째 챌린지");
        assertThat(challenges.get(2).getTitle()).isEqualTo("첫번째 챌린지");
    }

    @Test
    @DisplayName("다른 유저의 챌린지는 조회되지 않아야 함")
    void findAllByUserOrderByIdDesc_ShouldNotIncludeOtherUsersChallenges() {
        // given
        User anotherUser = new User(null, "다른사용자");
        entityManager.persist(anotherUser);

        LastChallenge anotherChallenge = createLastChallenge(
                "다른 유저의 챌린지",
                "다른 유저의 설명",
                LocalDate.of(2025, 6, 1),
                LocalDate.of(2025, 6, 10),
                anotherUser
        );
        entityManager.persist(anotherChallenge);
        entityManager.flush();

        // when
        List<LastChallenge> challenges = lastChallengeRepository.findAllByUserOrderByIdDesc(user);
        List<LastChallenge> anotherChallenges = lastChallengeRepository.findAllByUserOrderByIdDesc(anotherUser);

        // then
        assertThat(challenges).hasSize(3);
        assertThat(anotherChallenges).hasSize(1);

        assertThat(challenges).allMatch(challenge -> challenge.getUser().equals(user));
        assertThat(anotherChallenges).allMatch(challenge -> challenge.getUser().equals(anotherUser));

        assertThat(anotherChallenges.get(0).getTitle()).isEqualTo("다른 유저의 챌린지");
    }

    @Test
    @DisplayName("챌린지가 없는 유저의 경우 빈 리스트 반환 확인")
    void findAllByUserOrderByIdDesc_ShouldReturnEmptyListWhenNoChallenge() {
        // given
        User emptyUser = new User(null, "챌린지없는사용자");
        entityManager.persist(emptyUser);
        entityManager.flush();

        // when
        List<LastChallenge> challenges = lastChallengeRepository.findAllByUserOrderByIdDesc(emptyUser);

        // then
        assertThat(challenges).isEmpty();
    }
}
