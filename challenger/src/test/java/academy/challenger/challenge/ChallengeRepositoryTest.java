package academy.challenger.challenge;

import academy.challenger.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ChallengeRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ChallengeRepository challengeRepository;

    private User user;
    private Challenge challenge;
    private LocalDate now;

    @BeforeEach
    void setUp() {
        now = LocalDate.now();
        user = new User(null, "테스트사용자");
        entityManager.persist(user);
        
        challenge = new Challenge(user, "테스트 챌린지", "테스트 설명", now, 30);
        entityManager.persist(challenge);
        entityManager.flush();
    }

    @Test
    void 사용자로_챌린지_찾기_성공() {
        // when
        List<Challenge> challenges = challengeRepository.findAllByUser(user);

        // then
        assertThat(challenges).hasSize(1);
        assertThat(challenges.get(0).getTitle()).isEqualTo("테스트 챌린지");
        assertThat(challenges.get(0).getDescription()).isEqualTo("테스트 설명");
        assertThat(challenges.get(0).getStartDate()).isEqualTo(now);
        assertThat(challenges.get(0).getDuration()).isEqualTo(30);
        assertThat(challenges.get(0).getUser().getId()).isEqualTo(user.getId());
    }

    @Test
    void 사용자로_챌린지_찾기_없음() {
        // given
        User otherUser = new User(null, "다른사용자");
        entityManager.persist(otherUser);
        entityManager.flush();

        // when
        List<Challenge> challenges = challengeRepository.findAllByUser(otherUser);

        // then
        assertThat(challenges).isEmpty();
    }

    @Test
    void 챌린지_저장() {
        // given
        User newUser = new User(null, "새사용자");
        entityManager.persist(newUser);
        
        Challenge newChallenge = new Challenge(newUser, "새로운 챌린지", "새로운 설명", now, 60);

        // when
        Challenge savedChallenge = challengeRepository.save(newChallenge);

        // then
        assertThat(savedChallenge.getId()).isNotNull();
        assertThat(savedChallenge.getTitle()).isEqualTo("새로운 챌린지");
        assertThat(savedChallenge.getDescription()).isEqualTo("새로운 설명");
        assertThat(savedChallenge.getStartDate()).isEqualTo(now);
        assertThat(savedChallenge.getDuration()).isEqualTo(60);
        assertThat(savedChallenge.getUser().getId()).isEqualTo(newUser.getId());
    }

    @Test
    void 챌린지_조회() {
        // when
        Challenge foundChallenge = challengeRepository.findById(challenge.getId()).orElse(null);

        // then
        assertThat(foundChallenge).isNotNull();
        assertThat(foundChallenge.getTitle()).isEqualTo("테스트 챌린지");
        assertThat(foundChallenge.getDescription()).isEqualTo("테스트 설명");
        assertThat(foundChallenge.getStartDate()).isEqualTo(now);
        assertThat(foundChallenge.getDuration()).isEqualTo(30);
    }

    @Test
    void 챌린지_삭제() {
        // when
        challengeRepository.delete(challenge);
        Challenge deletedChallenge = entityManager.find(Challenge.class, challenge.getId());

        // then
        assertThat(deletedChallenge).isNull();
    }
}
