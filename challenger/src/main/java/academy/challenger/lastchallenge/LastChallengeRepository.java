package academy.challenger.lastchallenge;

import academy.challenger.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LastChallengeRepository extends JpaRepository<LastChallenge, Long> {
    List<LastChallenge> findAllByUser(User user);
}
