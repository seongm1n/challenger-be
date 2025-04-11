package academy.challenger.user;

import jakarta.persistence.Entity;
import lombok.Getter;

@Entity
@Getter
public class User {
    private final Long id;
    private final String name;

    public User(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
