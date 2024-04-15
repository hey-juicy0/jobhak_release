package univcapstone.employmentsite.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import univcapstone.employmentsite.domain.Career;
import univcapstone.employmentsite.domain.Experience;
import univcapstone.employmentsite.domain.User;

import java.util.Date;

@Data
@Getter
@Setter
public class ExpSaveDto {
    private String expName;
    private Date startDate;
    private Date endDate;
    private String expContent;

    public Experience toEntity(User user, ExpSaveDto expSaveDto) {
        return Experience.builder()
                .user(user)
                .expName(expSaveDto.getExpName())
                .startDate(expSaveDto.getStartDate())
                .endDate(expSaveDto.getEndDate())
                .expContent(expSaveDto.getExpContent())
                .build();
    }
}
