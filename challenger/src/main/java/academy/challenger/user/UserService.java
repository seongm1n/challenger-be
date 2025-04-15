package academy.challenger.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponse save(UserRequest request) {
        User user = userRepository.findByName(request.username());
        if (user == null) {
            user = new User(null, request.username());
            userRepository.save(user);
        }

        return new UserResponse(user.getId(), user.getName());
    }
}
