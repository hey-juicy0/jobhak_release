package univcapstone.employmentsite.controller;

import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import univcapstone.employmentsite.domain.Bookmark;
import univcapstone.employmentsite.domain.Picture;
import univcapstone.employmentsite.domain.User;
import univcapstone.employmentsite.dto.*;
import univcapstone.employmentsite.service.AuthService;
import univcapstone.employmentsite.service.BookmarkService;
import univcapstone.employmentsite.service.PictureService;
import univcapstone.employmentsite.service.UserService;
import univcapstone.employmentsite.token.CustomUserDetails;
import univcapstone.employmentsite.token.TokenProvider;
import univcapstone.employmentsite.util.response.BasicResponse;
import univcapstone.employmentsite.util.response.DefaultResponse;
import univcapstone.employmentsite.util.response.ErrorResponse;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class MyPageController {

    private final UserService userService;
    private final BookmarkService bookmarkService;
    private final PictureService pictureService;
    private final AuthService authService;

    @Value("${aws.s3.profile.dirName}")
    private String dirName;

    @Value("${spring.security.oauth2.client.provider.naver.token-uri}")
    private String tokenUri;

    @Value("${aws.s3.bucket}")
    private String bucket;

    public MyPageController(UserService userService, BookmarkService bookmarkService,
                            PictureService pictureService,
                            AuthService authService) {
        this.userService = userService;
        this.bookmarkService = bookmarkService;
        this.pictureService=pictureService;
        this.authService = authService;
    }

    @GetMapping("/user/myInfo")
    public ResponseEntity<? extends BasicResponse> myInfo(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        User user = customUserDetails.getUser();

        DefaultResponse<User> defaultResponse = DefaultResponse.<User>builder()
                .code(HttpStatus.OK.value())
                .httpStatus(HttpStatus.OK)
                .message("유저 마이페이지")
                .result(user)
                .build();

        return ResponseEntity.ok().body(defaultResponse);
    }

    //프로필 사진만 가져오기(URL)
    @GetMapping("user/image/show")
    public ResponseEntity<? extends BasicResponse> getMyProfile(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        User user = customUserDetails.getUser();
        String image=pictureService.getProfileImageOne(user);

        DefaultResponse<String> defaultResponse = DefaultResponse.<String>builder()
                .code(HttpStatus.OK.value())
                .httpStatus(HttpStatus.OK)
                .message("사용자의 프로필 사진")
                .result(image)
                .build();

        return ResponseEntity.ok()
                .body(defaultResponse);
    }

    //프로필 사진 저장
    @PostMapping(value = "/user/image/save", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<? extends BasicResponse> saveProfile(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            HttpServletRequest request,
            @RequestPart(value = "files") MultipartFile multipartFiles) throws IOException {

        try {
            User user = customUserDetails.getUser();
            String uploadImagesUrl = pictureService.uploadProfileFile(multipartFiles, dirName,user);

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

    //현재 프로필 사진 삭제
    @DeleteMapping(value ="/user/image/delete")
    public ResponseEntity<? extends BasicResponse> deleteProfile(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        List<Picture> pictures=pictureService.getAllProfileImageName(customUserDetails.getUser());
        for(Picture picture : pictures){
            if(picture.isProfile()){
                pictureService.deleteFile(picture);
            }
        }

        return ResponseEntity.ok(
                DefaultResponse.builder()
                        .code(HttpStatus.OK.value())
                        .httpStatus(HttpStatus.OK)
                        .message("사진 삭제 완료")
                        .result("")
                        .build()
        );
    }

    //프로필 사진 업데이트
    @PatchMapping("/user/image/update")
    public ResponseEntity<? extends BasicResponse> updateProfile(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestPart(value = "files") MultipartFile multipartFiles
    ) throws IOException {
        User user = customUserDetails.getUser();
        List<Picture> pictures=pictureService.getAllProfileImageName(user);
        for(Picture picture : pictures){
            if(picture.isProfile()){
                pictureService.deleteFile(picture);
            }
        }
        String uploadImagesUrl = pictureService.uploadProfileFile(multipartFiles, dirName,user);

        return ResponseEntity.ok(
                DefaultResponse.builder()
                        .code(HttpStatus.OK.value())
                        .httpStatus(HttpStatus.OK)
                        .message("사진 업데이트 완료")
                        .result(uploadImagesUrl)
                        .build()
        );
    }
    //합성(만들어진) 사진 모두를 가져오기
    @GetMapping("/user/picture")
    public ResponseEntity<? extends BasicResponse> myPicture(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        User user = customUserDetails.getUser();
        List<Map<String,String>> image=pictureService.getConversionImage(user);
        DefaultResponse<List<Map<String,String>>> defaultResponse = DefaultResponse.<List<Map<String,String>>>builder()
                .code(HttpStatus.OK.value())
                .httpStatus(HttpStatus.OK)
                .message("아마존에서 온 합성된 사진들")
                .result(image)
                .build();

        return ResponseEntity.ok()
                .body(defaultResponse);
    }
    @GetMapping("/user/bookmark")
    public ResponseEntity<? extends BasicResponse> myBookmark(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {

        User user = customUserDetails.getUser();

        //나의 북마크
        List<Bookmark> bookmarks = bookmarkService.getMyBookmark(user.getId());
        List<BookmarkToFront> bookmarkToFront = new ArrayList<>();
        for (Bookmark bookmark : bookmarks) {
            bookmarkToFront.add(new BookmarkToFront(
                    bookmark.getBookmarkId(),
                    bookmark.getUser().getId(),
                    bookmark.getUser().getLoginId(),
                    bookmark.getUser().getName(),
                    bookmark.getPost().getPostId(),
                    bookmark.getPost().getTitle(),
                    bookmark.getPost().getCategory(),
                    bookmark.getPost().getDate()
            ));
        }
        log.info("찾은 자기의 북마크={}", bookmarkToFront);

        DefaultResponse<List<BookmarkToFront>> defaultResponse = DefaultResponse.<List<BookmarkToFront>>builder()
                .code(HttpStatus.OK.value())
                .httpStatus(HttpStatus.OK)
                .message("북마크 가져오기 완료")
                .result(bookmarkToFront)
                .build();

        return ResponseEntity.ok().body(defaultResponse);
    }

    @PatchMapping(value = "/user/edit/pw")
    public ResponseEntity<? extends BasicResponse> editPw(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody @Validated UserDeleteDto userDeleteDto
    ) {

        User user = customUserDetails.getUser();

        String newPassword = userDeleteDto.getPassword();

        userService.editPass(user, newPassword);

        DefaultResponse<String> defaultResponse = DefaultResponse.<String>builder()
                .code(HttpStatus.OK.value())
                .httpStatus(HttpStatus.OK)
                .message("비밀번호 변경 완료")
                .result("")
                .build();

        return ResponseEntity.ok().body(defaultResponse);

    }

    @PatchMapping(value = "/user/edit")
    public ResponseEntity<? extends BasicResponse> editNickname(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody @Validated NicknameDto nicknameDto
    ) {

        User user = customUserDetails.getUser();

        String newNickname = nicknameDto.getNickname();

        userService.editNickname(user, newNickname);

        DefaultResponse<String> defaultResponse = DefaultResponse.<String>builder()
                .code(HttpStatus.OK.value())
                .httpStatus(HttpStatus.OK)
                .message("닉네임 변경 완료")
                .result("")
                .build();

        return ResponseEntity.ok().body(defaultResponse);

    }

    @DeleteMapping(value = "/user/delete")
    public ResponseEntity<? extends BasicResponse> deleteUser(
            HttpServletRequest request,
            @RequestBody @Validated UserDeleteDto userDeleteDto
    ) {
        try {
            log.info("삭제하려는 유저 데이터 {}", userDeleteDto);

            //리프레시 토큰 삭제
            authService.deleteRefreshToken(userDeleteDto.getLoginId());

            //DB에서 멤버 삭제
            userService.deleteUser(userDeleteDto);

            DefaultResponse<String> defaultResponse = DefaultResponse.<String>builder()
                    .code(HttpStatus.OK.value())
                    .httpStatus(HttpStatus.OK)
                    .message("계정 삭제 완료")
                    .result("")
                    .build();


            return ResponseEntity.ok().body(defaultResponse);
        } catch (IllegalStateException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(request.getServletPath(),
                            HttpStatus.BAD_REQUEST.value(),
                            "잘못된 삭제 요청입니다."));
        }
    }

//    @GetMapping(value = "/user/naver/delete")
//    public ResponseEntity<? extends BasicResponse> deleteNaverUser(@Validated NaverDto naverDto) throws URISyntaxException {
//
//        RestTemplate restTemplate = new RestTemplate();
//
//        naverDto.setGrantType("delete");
//
//        URI uri = new URI(tokenUri +
//                "?grant_type=" + naverDto.getGrantType() +
//                "&client_id=" + naverDto.getClientId() +
//                "&client_secret=" + naverDto.getClientSecret() +
//                "&access_token=" + naverDto.getAccessToken());
//
//        ResponseEntity<Object> response = restTemplate.exchange(uri, HttpMethod.POST, null, Object.class);
//
//        if (response != null) {
//            log.info("네이버 연동 해제에 성공했습니다.");
//        }
//
//
//
//
//    }

}
/*
@PatchMapping(value = "/user/edit")
public ResponseEntity<? extends BasicResponse> editUser(
        @RequestBody @Validated UserEditDto userEditData
) {
    try {
        log.info("수정려는 유저 데이터 {}", userEditData);
        userService.editUser(userEditData);

        DefaultResponse<String> defaultResponse = DefaultResponse.<String>builder()
                .code(HttpStatus.OK.value())
                .httpStatus(HttpStatus.OK)
                .message("계정 편집 완료")
                .result("")
                .build();

        return ResponseEntity.ok().body(defaultResponse);
    } catch (IllegalStateException e) {
        log.info("수정하려는 유저가 없거나 찾을 수 없습니다");
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                        "잘못된 수정 요청입니다."));
    }

}
*/