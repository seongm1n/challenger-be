package academy.challenger.user;

import academy.challenger.auth.WithMockCustomUser;
import academy.challenger.auth.security.JwtTokenProvider;
import academy.challenger.auth.security.LoginUserArgumentResolver;
import academy.challenger.config.JwtProperties;
import academy.challenger.config.TestSecurityConfig;
import academy.challenger.config.WebConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(TestSecurityConfig.class)
public class UserControllerTest {
    
    @MockBean
    private UserService userService;
    
    // 보안 관련 Mock Bean
    @MockBean
    private JwtTokenProvider jwtTokenProvider;
    
    @MockBean
    private LoginUserArgumentResolver loginUserArgumentResolver;
    
    @MockBean
    private JwtProperties jwtProperties;
    
    @MockBean
    private WebConfig webConfig;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UserRequest userRequest;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        userRequest = new UserRequest("테스트사용자");
        userResponse = new UserResponse(1L, "테스트사용자");
        
        given(userService.save(any(UserRequest.class))).willReturn(userResponse);
    }

    @Test
    @WithMockUser
    void 사용자_생성_성공() throws Exception {
        // when & then
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userResponse.id()))
                .andExpect(jsonPath("$.username").value(userResponse.username()));
    }
    
    @Test
    @WithMockUser
    void 사용자_생성_실패_유저명누락() throws Exception {
        // given
        UserRequest invalidRequest = new UserRequest("");
        
        // when & then
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @WithMockUser
    void 사용자_생성_실패_유저명널() throws Exception {
        // given
        String requestJson = "{\"username\": null}";
        
        // when & then
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
