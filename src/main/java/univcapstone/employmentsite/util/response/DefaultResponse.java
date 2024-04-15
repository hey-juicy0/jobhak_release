package univcapstone.employmentsite.util.response;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class DefaultResponse<T> extends BasicResponse {
    private Integer code;
    private HttpStatus httpStatus;
    private String message;
    private T result;
}
