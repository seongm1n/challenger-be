package academy.challenger.challenge;

import academy.challenger.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
public class Challenge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private String title;
    private String description;
    private LocalDate startDate;
    private Integer duration;

    public Challenge(User user, String title, String description, LocalDate startDate, Integer duration) {
        this.user = user;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.duration = duration;
    }
}
