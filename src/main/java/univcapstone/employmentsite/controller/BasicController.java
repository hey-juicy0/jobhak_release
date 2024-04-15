package univcapstone.employmentsite.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.HttpStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import univcapstone.employmentsite.domain.User;
import univcapstone.employmentsite.dto.ImageAPIHrefDto;
import univcapstone.employmentsite.dto.JobResponseDto;
import univcapstone.employmentsite.service.UserService;
import univcapstone.employmentsite.token.CustomUserDetails;
import univcapstone.employmentsite.util.response.BasicResponse;
import univcapstone.employmentsite.util.response.DefaultResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


@Slf4j
@RestController
@RequiredArgsConstructor
public class BasicController {

    private final UserService userService;

    @GetMapping("/home/saramin")
    public ResponseEntity<List<JobResponseDto.Job>> saramin() throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = "https://oapi.saramin.co.kr/job-search?access-key=pEyjyJB3XnowAZP5ImZUuNbcGwGGDbUGQXQfdDZqhSFgPkBXKWq&bbs_gb=1&sr=directhire&job_type=1,10&loc_cd=117000&sort=rc&start=0&count=12&fields=expiration-date"; // 취업 사이트의 API URL
        ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);
        log.info("응답된 내용 = {}", response);
        String responseBody = response.getBody();

        ObjectMapper mapper = new ObjectMapper();
        JobResponseDto jobResponse = mapper.readValue(responseBody, JobResponseDto.class);
        List<JobResponseDto.Job> jobs = jobResponse.getJobs().getJob();
        for (int i=0; i<jobs.size(); i++) {
            jobs.get(i).setDday(jobs.get(i).getExpirationDate().toString());
        }

        return ResponseEntity.ok()
                .body(jobs);
    }

    @GetMapping("/home/saramin/href")
    public ResponseEntity<List<String>> extractImages() throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = "https://oapi.saramin.co.kr/job-search?access-key=pEyjyJB3XnowAZP5ImZUuNbcGwGGDbUGQXQfdDZqhSFgPkBXKWq&bbs_gb=1&sr=directhire&job_type=1,10&loc_cd=117000&sort=rc&start=0&count=12&fields=expiration-date"; // 취업 사이트의 API URL
        ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);
        log.info("응답된 내용 = {}", response);
        String responseBody = response.getBody();

        ObjectMapper mapper = new ObjectMapper();
        List<String> imageUrlList = new ArrayList<>();

        if (responseBody != null) {
            ImageAPIHrefDto imageAPIResponse = mapper.readValue(responseBody, ImageAPIHrefDto.class);

            List<ImageAPIHrefDto.Job> jobs = imageAPIResponse.getJobs().getJob();
            log.info("가져온 job의 갯수 = {}",jobs.size());
            for (ImageAPIHrefDto.Job job : jobs) {
                String href = job.getCompany().getDetail().getHref();
                log.info("href = {}",href);
                try {
                    if (href != null) {
                        Document document = Jsoup.connect(href).get();
                        Element imgElement = document.select(".box_logo img").first(); // box_logo class를 가지는 첫번째 div 태그 선택
                        if(imgElement!=null){
                            String src = imgElement.attr("src");
                            log.info("img 태그 = {}",imgElement);
                            imageUrlList.add(src);
                        }else{
                            imageUrlList.add(null);
                        }
                    }
                    else{
                        imageUrlList.add(null);
                    }
                } catch (HttpStatusException e) {
                    imageUrlList.add(null);
                }
            }
        }

        log.info("이미지 URL 데이터 = {}", imageUrlList);
        return ResponseEntity.ok()
                .body(imageUrlList);
    }

    @GetMapping("/home")
    public ResponseEntity<? extends BasicResponse> home(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {

        User user = userService.findUserByLoginId(customUserDetails.getUsername());

        log.info("current user = {}", user.getLoginId());

        if (user == null) {
            log.info("유저를 찾을 수 없습니다.");
        }

        DefaultResponse<User> defaultResponse = DefaultResponse.<User>builder()
                .code(HttpStatus.OK.value())
                .httpStatus(HttpStatus.OK)
                .message("홈페이지")
                .result(user)
                .build();

        return ResponseEntity.ok()
                .body(defaultResponse);
    }
}
