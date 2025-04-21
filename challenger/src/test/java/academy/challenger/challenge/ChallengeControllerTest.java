package academy.challenger.challenge;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChallengeController.class)
public class ChallengeControllerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public ChallengeService challengeService() {
            return new ChallengeService(null, null) {
                @Override
                public ChallengeResponse save(ChallengeRequest request) {
                    return new ChallengeResponse(1L, 1L, "테스트 챌린지", "테스트 설명", 0.0, 30);
                }
                
                @Override
                public List<ChallengeResponse> getAllById(long id) {
                    return List.of(
                        new ChallengeResponse(1L, 1L, "테스트 챌린지", "테스트 설명", 0.0, 30)
                    );
                }
                
                @Override
                public void delete(Long id) {
                    // 테스트에서는 별도 구현 필요 없음
                }
            };
        }
    }
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private ChallengeRequest challengeRequest;
    private ChallengeResponse challengeResponse;
    
    @BeforeEach
    void setUp() {
        challengeRequest = new ChallengeRequest(1L, "테스트 챌린지", "테스트 설명", 30);
        challengeResponse = new ChallengeResponse(1L, 1L, "테스트 챌린지", "테스트 설명", 0.0, 30);
    }
    
    @Test
    void 챌린지_생성_성공() throws Exception {
        // when & then
        mockMvc.perform(post("/challenges")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(challengeRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(challengeResponse.id()))
                .andExpect(jsonPath("$.userId").value(challengeResponse.userId()))
                .andExpect(jsonPath("$.title").value(challengeResponse.title()))
                .andExpect(jsonPath("$.description").value(challengeResponse.description()))
                .andExpect(jsonPath("$.progress").value(challengeResponse.progress()))
                .andExpect(jsonPath("$.duration").value(challengeResponse.duration()));
    }
    
    @Test
    void 챌린지_조회_성공() throws Exception {
        // when & then
        mockMvc.perform(get("/challenges/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(challengeResponse.id()))
                .andExpect(jsonPath("$[0].userId").value(challengeResponse.userId()))
                .andExpect(jsonPath("$[0].title").value(challengeResponse.title()))
                .andExpect(jsonPath("$[0].description").value(challengeResponse.description()))
                .andExpect(jsonPath("$[0].progress").value(challengeResponse.progress()))
                .andExpect(jsonPath("$[0].duration").value(challengeResponse.duration()));
    }
    
    @Test
    void 챌린지_삭제_성공() throws Exception {
        // when & then
        mockMvc.perform(delete("/challenges/1"))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
    
    @Test
    void 챌린지_생성_실패_제목누락() throws Exception {
        // given
        ChallengeRequest invalidRequest = new ChallengeRequest(1L, "", "테스트 설명", 30);
        
        // when & then
        mockMvc.perform(post("/challenges")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void 챌린지_생성_실패_설명누락() throws Exception {
        // given
        ChallengeRequest invalidRequest = new ChallengeRequest(1L, "테스트 챌린지", "", 30);
        
        // when & then
        mockMvc.perform(post("/challenges")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void 챌린지_생성_실패_기간누락() throws Exception {
        // given
        String requestJson = "{\"userId\":1,\"title\":\"테스트 챌린지\",\"description\":\"테스트 설명\",\"duration\":null}";
        
        // when & then
        mockMvc.perform(post("/challenges")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
