package academy.challenger.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void 이름으로_사용자_찾기_성공() {
        // given
        User user = new User(null, "테스트사용자");
        entityManager.persist(user);
        entityManager.flush();

        // when
        User foundUser = userRepository.findByName("테스트사용자");

        // then
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getName()).isEqualTo("테스트사용자");
    }

    @Test
    void 이름으로_사용자_찾기_실패() {
        // given
        User user = new User(null, "테스트사용자");
        entityManager.persist(user);
        entityManager.flush();

        // when
        User foundUser = userRepository.findByName("존재하지않는사용자");

        // then
        assertThat(foundUser).isNull();
    }

    @Test
    void 사용자_저장() {
        // given
        User user = new User(null, "새사용자");

        // when
        User savedUser = userRepository.save(user);

        // then
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getName()).isEqualTo("새사용자");
    }
}
