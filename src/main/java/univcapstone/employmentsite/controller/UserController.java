package univcapstone.employmentsite.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import univcapstone.employmentsite.domain.User;
import univcapstone.employmentsite.dto.MailDto;
import univcapstone.employmentsite.dto.UserEditDto;
import univcapstone.employmentsite.dto.UserFindDto;
import univcapstone.employmentsite.service.MailService;
import univcapstone.employmentsite.service.OtpService;
import univcapstone.employmentsite.token.CustomUserDetails;
import univcapstone.employmentsite.util.response.BasicResponse;
import univcapstone.employmentsite.util.response.ErrorResponse;
import univcapstone.employmentsite.service.UserService;
import univcapstone.employmentsite.util.response.DefaultResponse;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final MailService mailService;
    private final OtpService otpService;
    private final int otpLength = 10;

    @GetMapping("/join")
    public String join() {
        return "Hello join page"; // 회원가입 폼
    }

    @GetMapping("/joincheck")
    public String joinCheck() {
        return "join check list";
    }

    @PostMapping("/login/kakao/changeName")
    public ResponseEntity<User> changeName(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                           @RequestBody Map<String, String> data) {

        User user = customUserDetails.getUser();

        String updatedName = userService.updateName(user.getId(), data.get("name"));

        log.info("변경된 이름 = {}", updatedName);

        return ResponseEntity.ok()
                .body(user);
    }

    @PostMapping("/verify/id")
    public ResponseEntity<? extends BasicResponse> verifyID(
            HttpServletRequest request,
            @RequestBody @Validated UserFindDto userFindData
    ) {
        try {
            userService.validateDuplicateLoginId(userFindData.getLoginId());

            log.info("사용 가능한 아이디 = {}", userFindData.getLoginId());
            // 중복이 없는 경우, 사용 가능한 ID로 간주
            DefaultResponse<String> defaultResponse = DefaultResponse.<String>builder()
                    .code(HttpStatus.OK.value())
                    .httpStatus(HttpStatus.OK)
                    .message("ID 사용 가능")
                    .result(null)
                    .build();

            return ResponseEntity.ok()
                    .body(defaultResponse);

        } catch (IllegalStateException e) {
            log.error("중복된 아이디 존재 = {}",userFindData.getLoginId());
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(request.getServletPath(), HttpStatus.BAD_REQUEST.value(), "이미 ID가 존재합니다."));
        }
    }

    @GetMapping("/find/id")
    public String findID() {
        return "findID"; //아이디 찾기 페이지
    }

    @PostMapping("/find/id")
    public ResponseEntity<? extends BasicResponse> findID(
            HttpServletRequest request,
            @RequestBody @Validated UserFindDto userFindData
    ) {
        User user = userService.findId(userFindData.getName(),
                userFindData.getEmail());

        if (user == null) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(request.getServletPath(),
                            HttpStatus.BAD_REQUEST.value(),
                            "해당하는 이름과 Email의 계정이 없습니다."));
        } else {
            log.info("이름과 이메일로 찾은 아이디 = {}", user.getLoginId());
            DefaultResponse<String> defaultResponse = DefaultResponse.<String>builder()
                    .code(HttpStatus.OK.value())
                    .httpStatus(HttpStatus.OK)
                    .message("ID를 찾았습니다.")
                    .result(user.getLoginId())
                    .build();

            return ResponseEntity.ok()
                    .body(defaultResponse);
        }
    }

    @PostMapping("/find/pw")
    public ResponseEntity<? extends BasicResponse> findPassword(
            HttpServletRequest request,
            @RequestBody @Validated UserFindDto userFindData
    ) {
        log.info("loginId = {}, name = {}, email = {}", userFindData.getLoginId(), userFindData.getName(), userFindData.getEmail());

        //비밀번호 찾기에 대한 로직
        User user = userService.findUserByLoginIdAndNameAndEmail(
                userFindData.getLoginId(),
                userFindData.getName(),
                userFindData.getEmail());

        if (user == null) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(request.getServletPath(),
                            HttpStatus.BAD_REQUEST.value(),
                            "유저를 찾을 수 없습니다."));
        }

        log.info("아이디와 이름, 이메일로 찾은 사용자 로그인 ID = {}", user.getLoginId());

        //1. 임시 비밀번호를 생성함
        //2. 해당 임시 비밀번호를 key: 사용자 id, value: 임시 비밀번호 구조로 redis에 저장한다.
        //3. 임시 번호를 사용자의 이메일로 보낸다.
        //4. N분 후 임시 비밀번호를 파기한다.

        // //사용자는 자신의 원래 비밀번호를 맞게 입력하거나 (제한 시간 이내로)
        // 사용자는 임시 비밀번호를 입력하면 로그인을 할 수 있다. 임시 비밀번호 발급 이후에는 반드시 비밀번호를 변경해야 한다.

        String otp = otpService.saveOtp(user.getId(), otpLength);

        mailService.sendMail(MailDto.builder()
                .receiver(user.getEmail())
                .subject("임시 비밀번호 입니다.")
                .content(otp)
                .build());

        DefaultResponse<String> defaultResponse = DefaultResponse.<String>builder()
                .code(HttpStatus.OK.value())
                .httpStatus(HttpStatus.OK)
                .message("사용자의 이메일로 임시 비밀번호를 전송했습니다.")
                .result(user.getEmail())
                .build();

        return ResponseEntity.ok().body(defaultResponse);

    }

    @PostMapping(value = "/reset/pw", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<? extends BasicResponse> resetPassword(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody UserEditDto userEditDto) {

        User user = customUserDetails.getUser();
        String updatedPassword = userService.updatePassword(user.getId(), userEditDto.getPassword());

        log.info("변경된 유저 = {}, 변경된 유저의 비밀번호 = {}", userEditDto.getLoginId(), updatedPassword);

        DefaultResponse<UserEditDto> defaultResponse = DefaultResponse.<UserEditDto>builder()
                .code(HttpStatus.OK.value())
                .httpStatus(HttpStatus.OK)
                .message("비밀번호 변경완료.")
                .result(userEditDto)
                .build();

        return ResponseEntity.ok().body(defaultResponse);
//        catch (PersistenceException e) {
//            return ResponseEntity.badRequest()
//                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
//                            "비밀번호 변경실패"));
//        }
    }
}
