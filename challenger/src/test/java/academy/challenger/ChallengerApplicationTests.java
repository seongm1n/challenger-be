package academy.challenger;

import academy.challenger.auth.security.JwtTokenProvider;
import academy.challenger.auth.security.LoginUserArgumentResolver;
import academy.challenger.config.JwtProperties;
import academy.challenger.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class ChallengerApplicationTests {

	@MockBean
	private JwtTokenProvider jwtTokenProvider;
	
	@MockBean
	private UserRepository userRepository;
	
	@MockBean
	private LoginUserArgumentResolver loginUserArgumentResolver;
	
	@MockBean
	private JwtProperties jwtProperties;

	@Test
	void contextLoads() {
	}

}
