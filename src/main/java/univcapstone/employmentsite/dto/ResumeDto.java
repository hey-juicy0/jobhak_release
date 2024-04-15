package univcapstone.employmentsite.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ResumeDto {
    @Nullable
    private Long resumeId;
    @NotEmpty
    private String content;
}
