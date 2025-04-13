package academy.challenger.lastchallenge;

import academy.challenger.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
public class LastChallenge {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String retrospection;
    private String assessment;

    public LastChallenge(User user, String title, String description, LocalDate startDate, LocalDate endDate, String retrospection, String assessment) {
        this.user = user;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.retrospection = retrospection;
        this.assessment = assessment;
    }
}
