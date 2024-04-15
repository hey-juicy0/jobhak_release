package univcapstone.employmentsite.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Resume {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="resume_id")
    private Long resumeId;

    @Column(name="user_id")
    private Long userId;

    private String resumeContent;

    public Resume(Long userId, String content) {
        this.resumeId=userId;
        this.resumeContent=content;
    }
}
