package univcapstone.employmentsite.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import univcapstone.employmentsite.domain.Picture;
import univcapstone.employmentsite.domain.User;
import univcapstone.employmentsite.service.PictureService;
import univcapstone.employmentsite.token.CustomUserDetails;
import univcapstone.employmentsite.util.response.BasicResponse;
import univcapstone.employmentsite.util.response.DefaultResponse;
import univcapstone.employmentsite.util.response.ErrorResponse;

import java.io.*;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class PictureController {
    private final PictureService pictureService;
    private final String dirName;
    private final String transformDir;

    public PictureController(PictureService pictureService,
                             @Value("${aws.s3.profile.dirName}") String dirName,
                             @Value("${aws.s3.idPhoto.dirName}") String transformDir) {
        this.pictureService = pictureService;
        this.dirName = dirName;
        this.transformDir=transformDir;
    }

    @GetMapping("/profile/male")
    public ResponseEntity<? extends BasicResponse> downloadMalePicture(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        User user = customUserDetails.getUser();
        Map<String,String> images=pictureService.getProfileImageName(user);

        log.info("images = {}",images);
        if(images.isEmpty()){
            log.info("이미지가 없어서 default 이미지 전송");
            String result="https://jobhakdasik2000-bucket.s3.ap-northeast-2.amazonaws.com/default/default.png";
            DefaultResponse<String> defaultResponse = DefaultResponse.<String>builder()
                    .code(HttpStatus.OK.value())
                    .httpStatus(HttpStatus.OK)
                    .message("임시 프로필")
                    .result(result)
                    .build();

            return ResponseEntity.ok()
                    .body(defaultResponse);
        }
        DefaultResponse<Map<String,String>> defaultResponse = DefaultResponse.<Map<String,String>>builder()
                .code(HttpStatus.OK.value())
                .httpStatus(HttpStatus.OK)
                .message("아마존에서 온 프로필")
                .result(images)
                .build();

        return ResponseEntity.ok()
                .body(defaultResponse);
    }

    @GetMapping("/profile/female")
    public ResponseEntity<? extends BasicResponse> femalePicture(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        User user = customUserDetails.getUser();
        Map<String,String> images=pictureService.getProfileImageName(user);

        log.info("images = {}",images);
        if(images.isEmpty()){
            log.info("이미지가 없어서 default 이미지 전송");
            String result="https://jobhakdasik2000-bucket.s3.ap-northeast-2.amazonaws.com/default/default.png";
            DefaultResponse<String> defaultResponse = DefaultResponse.<String>builder()
                    .code(HttpStatus.OK.value())
                    .httpStatus(HttpStatus.OK)
                    .message("임시 프로필")
                    .result(result)
                    .build();

            return ResponseEntity.ok()
                    .body(defaultResponse);
        }
        DefaultResponse<Map<String,String>> defaultResponse = DefaultResponse.<Map<String,String>>builder()
                .code(HttpStatus.OK.value())
                .httpStatus(HttpStatus.OK)
                .message("아마존에서 온 프로필")
                .result(images)
                .build();

        return ResponseEntity.ok()
                .body(defaultResponse);
    }

    @PostMapping("/profile/edit")
    public ResponseEntity<? extends BasicResponse> editPicture(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam("brightness") float brightness,
            @RequestParam("saturation") float saturation,
            @RequestParam("conversion") boolean conversion
    ) {
        // 1. Spring 에서 사진 아이디로 사진을 찾고,
        // 2. 찾은 사진과 Front에서 온 명도와 채도 등의 값들을 해당 사진을 Flask로 보냄
        // 3. Flask에서 GAN을 이용해서 데이터를 생성하던지, 명도 채도 값을 조절한 사진을 보냄
        // 4. Spring에서 Flask에서 온 사진을 받아 Front로 전달

        // 프로필 이미지 url을 가져오기
        Picture picture=pictureService.getProfilePicture(customUserDetails.getUser());

        // JSON 형식으로 줄 이미지,명도,채도,옷바꾸기 기능의 여부
        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        formData.add("file", picture.getUploadFileName());
        formData.add("brightness", brightness);
        formData.add("saturation", saturation);
        formData.add("conversion", conversion);

        // Flask server로 데이터를 주고 받기
        RestTemplate restTemplate = new RestTemplate();
        String flaskEndpoint = "http://localhost:12300/profile/edit";
        String result = restTemplate.postForObject(flaskEndpoint, formData, String.class);

        log.info("Flask에서 온 응답 {}", result);

        DefaultResponse<String> defaultResponse = DefaultResponse.<String>builder()
                .code(HttpStatus.OK.value())
                .httpStatus(HttpStatus.OK)
                .message("Flask에서 온 데이터")
                .result(result)
                .build();

        return ResponseEntity.ok()
                .body(defaultResponse);

    }

    // 사진 변환 혹은 합성이 된 사진을 저장하는 역할
    // 사진 1장을 저장하는 것으로..
    @PostMapping(value = "/profile/save", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<? extends BasicResponse> save(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            HttpServletRequest request,
            @RequestPart(value = "files") MultipartFile multipartFiles) throws IOException {

        try {
            User user = customUserDetails.getUser();
            String uploadImagesUrl = pictureService.uploadConversionFile(multipartFiles, transformDir,user);

            return ResponseEntity.ok(
                    DefaultResponse.builder()
                            .code(HttpStatus.OK.value())
                            .httpStatus(HttpStatus.OK)
                            .message("사진 업로드 완료")
                            .result(uploadImagesUrl)
                            .build()
            );
        } catch (FileNotFoundException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(request.getServletPath(),
                            HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }

    }

}
