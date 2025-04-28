package academy.challenger.user;

import academy.challenger.auth.security.LoginUser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> save(@Valid @RequestBody UserRequest request, @LoginUser User user) {
        UserResponse response = userService.save(request);
        log.info("User: {}", user.getName());
        log.info("User created with Name: {}", request.username());
        log.info("User created with ID: {}", response.id());
        return ResponseEntity.ok().body(response);
    }
}
