package academy.challenger.lastchallenge;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AIReviewService {
    private final ChatClient chatClient;

    public AIReviewService(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultSystem("""
                        나는 일정기간동안 도전을 설정하고 그 도전을 완료하기 위해 노력했어.
                        지금부터 내가 도전을 끝내고 난 뒤 회고를 작성할거야.
                        이 회고에 대한 평가를 200자 이내로 작성해줘.
                        """)
                .build();
    }

    public String getReview(String message, String userName) {
        try {
            return chatClient
                    .prompt(message)
                    .call()
                    .content();
        } catch (Exception e) {
            return "AI 리뷰을 생성하는 중 오류가 발생했습니다.";
        }
    }
}
