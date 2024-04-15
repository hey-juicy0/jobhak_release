package univcapstone.employmentsite.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Data
@Getter
@Setter
public class ImageAPIHrefDto {
    private Jobs jobs;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Jobs {
        private List<Job> job;
        public List<ImageAPIHrefDto.Job> getJob() {
            return job;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    @Setter
    public static class Job {
        private Company company;
        private String href;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Company {
        private Detail detail;

        public Detail getDetail() {
            return detail;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Detail {
        private String href;
        private String name;

        public String getHref() {
            return href;
        }
    }
}
