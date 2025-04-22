package academy.challenger.lastchallenge;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LastChallengeController.class)
public class LastChallengeControllerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public LastChallengeService lastChallengeService() {
            return new LastChallengeService(null, null, null, null) {
                @Override
                public LastChallengeResponse save(LastChallengeRequest request) {
                    return new LastChallengeResponse(1L, 1L, "테스트 챌린지", "테스트 설명", LocalDate.of(2025, 4, 10), LocalDate.of(2025, 4, 15), "테스트 회고", "테스트 응원");
                }

                @Override
                public List<LastChallengeResponse> getAllById(long id) {
                    return List.of(
                        new LastChallengeResponse(1L, 1L, "테스트 챌린지", "테스트 설명", LocalDate.of(2025, 4, 10), LocalDate.of(2025, 4, 15), "테스트 회고", "테스트 응원")
                    );
                }
            };
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private LastChallengeRequest lastChallengeRequest;
    private LastChallengeResponse lastChallengeResponse;

    @BeforeEach
    void setUp() {
        lastChallengeRequest = new LastChallengeRequest(1L, 1L, "테스트 회고");
        lastChallengeResponse = new LastChallengeResponse(1L, 1L, "테스트 챌린지", "테스트 설명", LocalDate.of(2025, 4, 10), LocalDate.of(2025, 4, 15), "테스트 회고", "테스트 응원");
    }

    @Test
    void 지난_챌린지_생성_성공() throws Exception {
        // when & then
        mockMvc.perform(post("/last-challenges")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(lastChallengeRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(lastChallengeResponse.id()))
                .andExpect(jsonPath("$.userId").value(lastChallengeResponse.userId()))
                .andExpect(jsonPath("$.title").value(lastChallengeResponse.title()))
                .andExpect(jsonPath("$.description").value(lastChallengeResponse.description()))
                .andExpect(jsonPath("$.startDate").value(lastChallengeResponse.startDate().toString()))
                .andExpect(jsonPath("$.endDate").value(lastChallengeResponse.endDate().toString()))
                .andExpect(jsonPath("$.retrospection").value(lastChallengeResponse.retrospection()))
                .andExpect(jsonPath("$.assessment").value(lastChallengeResponse.assessment()));
    }

    @Test
    void 지난_챌린지_조회_성공() throws Exception {
        // when & then
        mockMvc.perform(get("/last-challenges/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(lastChallengeResponse.id()))
                .andExpect(jsonPath("$[0].userId").value(lastChallengeResponse.userId()))
                .andExpect(jsonPath("$[0].title").value(lastChallengeResponse.title()))
                .andExpect(jsonPath("$[0].description").value(lastChallengeResponse.description()))
                .andExpect(jsonPath("$[0].startDate").value(lastChallengeResponse.startDate().toString()))
                .andExpect(jsonPath("$[0].endDate").value(lastChallengeResponse.endDate().toString()))
                .andExpect(jsonPath("$[0].retrospection").value(lastChallengeResponse.retrospection()))
                .andExpect(jsonPath("$[0].assessment").value(lastChallengeResponse.assessment()));
    }
}
