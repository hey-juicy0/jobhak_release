package univcapstone.employmentsite.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class BookmarkDeleteDto {
    @NotNull
    private Long bookmarkId;
}
