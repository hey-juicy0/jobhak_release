package univcapstone.employmentsite.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import univcapstone.employmentsite.domain.*;
import univcapstone.employmentsite.dto.CareerSaveDto;
import univcapstone.employmentsite.dto.ExpSaveDto;
import univcapstone.employmentsite.dto.ResumeDto;
import univcapstone.employmentsite.service.*;
import univcapstone.employmentsite.token.CustomUserDetails;
import univcapstone.employmentsite.util.response.BasicResponse;
import univcapstone.employmentsite.util.response.DefaultResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;
    private final CareerService careerService;
    private final ExpService expService;
    private final ExpNCareerService expNCareerService;

    // 경력 경험 목록으로 가져오기
    @GetMapping("/resume/get/myList")
    public ResponseEntity<? extends BasicResponse> getList(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) throws JsonProcessingException {
        User user = customUserDetails.getUser();
        List<Career> careers = careerService.findCareerListByUserId(user.getId());
        List<Experience> exps = expService.findExpListByUserId(user.getId());

        List<CareerSaveDto> careerToFront = new ArrayList<>();
        List<ExpSaveDto> expToFront = new ArrayList<>();
        for(Career career : careers){
            CareerSaveDto data=new CareerSaveDto();
            data.setCareerName(career.getCareerName());
            data.setStartDate(career.getStartDate());
            data.setEndDate(career.getEndDate());
            data.setCareerContent(career.getCareerContent());
            careerToFront.add(data);
        }
        for(Experience exp : exps){
            ExpSaveDto data=new ExpSaveDto();
            data.setExpName(exp.getExpName());
            data.setStartDate(exp.getStartDate());
            data.setEndDate(exp.getEndDate());
            data.setExpContent(exp.getExpContent());
            expToFront.add(data);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("careerToFront", careerToFront);
        map.put("expToFront", expToFront);
        DefaultResponse<Map<String, Object>> defaultResponse = DefaultResponse.<Map<String, Object>>builder()
                .code(HttpStatus.OK.value())
                .httpStatus(HttpStatus.OK)
                .message("경력,경험 목록으로 가져오기")
                .result(map)
                .build();

        return ResponseEntity.ok()
                .body(defaultResponse);
    }

    // 경력 경험 목록으로 저장하기
    @PostMapping("/resume/post/myList")
    public ResponseEntity<? extends BasicResponse> saveList(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestPart(value="careerSaveDto") List<CareerSaveDto> careerSaveDto,
            @RequestPart(value="expSaveDto") List<ExpSaveDto> expSaveDto
    ){
        User user = customUserDetails.getUser();
        careerService.saveCareer(user,careerSaveDto);
        expService.saveExp(user,expSaveDto);

        DefaultResponse<String> defaultResponse = DefaultResponse.<String>builder()
                .code(HttpStatus.OK.value())
                .httpStatus(HttpStatus.OK)
                .message("경력, 경험 목록 저장 완료")
                .result("")
                .build();

        return ResponseEntity.ok()
                .body(defaultResponse);
    }

    // 경력 경험 문단 가져오기
    @GetMapping("/resume/get/myText")
    public ResponseEntity<? extends BasicResponse> getText(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        User user = customUserDetails.getUser();
        ExpNCareer expNcareer = expNCareerService.findTextByUserId(user.getId());

        Map<String, String> map = new HashMap<>();
        map.put("userId", expNcareer.getUser().getLoginId());
        map.put("content", expNcareer.getContent());
        DefaultResponse<Map<String, String>> defaultResponse = DefaultResponse.<Map<String, String>>builder()
                .code(HttpStatus.OK.value())
                .httpStatus(HttpStatus.OK)
                .message("경력 경험 줄글 가져오기")
                .result(map)
                .build();

        return ResponseEntity.ok()
                .body(defaultResponse);
    }

    // 경력 경험 목록으로 저장하기
    @PostMapping("/resume/post/myText")
    public ResponseEntity<? extends BasicResponse> saveText(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody String content
    ){
        User user = customUserDetails.getUser();
        expNCareerService.saveText(user,content);

        DefaultResponse<String> defaultResponse = DefaultResponse.<String>builder()
                .code(HttpStatus.OK.value())
                .httpStatus(HttpStatus.OK)
                .message("경력 경험 줄글 저장하기")
                .result("")
                .build();

        return ResponseEntity.ok()
                .body(defaultResponse);
    }

    @GetMapping("resume/get/IsText")
    public ResponseEntity<? extends BasicResponse> isText(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        User user = customUserDetails.getUser();
        boolean isText = false;

        List<Career> careers = careerService.findCareerListByUserId(user.getId());
        List<Experience> exps = expService.findExpListByUserId(user.getId());
        if(careers == null && exps == null){
            isText = true;
        }
        DefaultResponse<Boolean> defaultResponse = DefaultResponse.<Boolean>builder()
                .code(HttpStatus.OK.value())
                .httpStatus(HttpStatus.OK)
                .message("경력 경험 줄글인지 아닌지 (경험 경력 목록이 아무것도 없을 때)")
                .result(isText)
                .build();

        return ResponseEntity.ok()
                .body(defaultResponse);
    }

    /*
    @GetMapping(value = "/resume/write")
    public ResponseEntity<? extends BasicResponse> resumeWrite() {
        //자기소개서 첫화면 불러오기
        DefaultResponse<String> defaultResponse = DefaultResponse.<String>builder()
                .code(HttpStatus.OK.value())
                .httpStatus(HttpStatus.OK)
                .message("자기소개서 첫화면")
                .result("")
                .build();

        return ResponseEntity.ok()
                .body(defaultResponse);
    }

    @GetMapping(value = "/resume/revise")
    public ResponseEntity<? extends BasicResponse> getResumeRevise(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {

        User user = customUserDetails.getUser();

        //자기소개서 수정하기 불러오기
        List<Resume> resumes = resumeService.getMyResume(user.getId());
        log.info("불러온 자기소개서들 중 첫 번째 ID= {}", resumes.get(0).getResumeId());

        DefaultResponse<List<Resume>> defaultResponse = DefaultResponse.<List<Resume>>builder()
                .code(HttpStatus.OK.value())
                .httpStatus(HttpStatus.OK)
                .message("수정할 자기소개서 가져오기 완료")
                .result(resumes)
                .build();

        return ResponseEntity.ok()
                .body(defaultResponse);
    }

    @PostMapping(value = "/resume/revise")
    public ResponseEntity<? extends BasicResponse> postResumeRevise(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody @Validated ResumeDto resumeDto
    ) {

        User user = customUserDetails.getUser();

        //자기소개서 수정하기
        resumeService.reviseResume(resumeDto.getResumeId(), resumeDto.getContent());
        log.info("수정자: {}, 수정 내용 {}", user.getLoginId(), resumeDto);

        DefaultResponse<String> defaultResponse = DefaultResponse.<String>builder()
                .code(HttpStatus.OK.value())
                .httpStatus(HttpStatus.OK)
                .message("자기소개서 수정 완료")
                .result("")
                .build();

        return ResponseEntity.ok()
                .body(defaultResponse);
    }

    @PostMapping(value = "/resume/save")
    public ResponseEntity<? extends BasicResponse> resumeSave(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody @Validated ResumeDto resumeDto
    ) {

        User user = customUserDetails.getUser();

        //자기소개서 저장하기
        Resume resume = resumeService.saveResume(user, resumeDto.getContent());

        log.info("저장하려는 사람 {}, 저장하는 내용 {}", user.getLoginId(), resumeDto);

        DefaultResponse<Resume> defaultResponse = DefaultResponse.<Resume>builder()
                .code(HttpStatus.OK.value())
                .httpStatus(HttpStatus.OK)
                .message("자기소개서 저장 완료")
                .result(resume)
                .build();

        return ResponseEntity.ok()
                .body(defaultResponse);

    }
    */
}