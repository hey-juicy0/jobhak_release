package univcapstone.employmentsite.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Data
@Getter@Setter
public class JobResponseDto {
    private Jobs jobs;

    public Jobs getJobs() {
        return jobs;
    }

    public void setJobs(Jobs jobs) {
        this.jobs = jobs;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Jobs {
        private List<Job> job;

        public List<Job> getJob() {
            return job;
        }
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    public static class Job {
        private String url;
        private int active;
        private Object company;
        private Object position;
        @JsonProperty("expiration-date")
        private String expirationDate;
        @JsonIgnoreProperties(ignoreUnknown = true)
        private double Dday;

        private double getDateDifference(Date startDate,Date endDate){
            long difference = endDate.getTime() - startDate.getTime();
            return Math.ceil(difference / (1000 * 60 * 60 * 24));
        }
        public void setDday(String exDay){
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date today = new Date();
                Date exDate = sdf.parse(exDay);
                double differ = getDateDifference(today, exDate);
                this.Dday = differ;
            }catch (ParseException e) {
                // 날짜 포맷이 잘못되었을 때의 예외 처리
                e.printStackTrace();
            }
        }

    }
}
