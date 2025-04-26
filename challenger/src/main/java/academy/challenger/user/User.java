package academy.challenger.user;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    public User(Long id, String name) {
        this.id = id;
        this.name = name;
    }
    
    @Builder
    public User(Long id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }
    
    public boolean matchPassword(String rawPassword) {
        // 실제로는 PasswordEncoder를 사용해야 함
        return this.password.equals(rawPassword);
    }
}
