package univcapstone.employmentsite.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import univcapstone.employmentsite.domain.Career;
import univcapstone.employmentsite.domain.Post;
import univcapstone.employmentsite.domain.User;

import java.util.Date;

@Data
@Getter
@Setter
public class CareerSaveDto {
    private String careerName;
    private Date startDate;
    private Date endDate;
    private String careerContent;

    public Career toEntity(User user, CareerSaveDto careerSaveDto) {
        return Career.builder()
                .user(user)
                .careerName(careerSaveDto.getCareerName())
                .startDate(careerSaveDto.getStartDate())
                .endDate(careerSaveDto.getEndDate())
                .careerContent(careerSaveDto.getCareerContent())
                .build();
    }
}
