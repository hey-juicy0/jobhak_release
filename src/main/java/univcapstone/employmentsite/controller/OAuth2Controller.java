package univcapstone.employmentsite.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import univcapstone.employmentsite.dto.TokenDto;
import univcapstone.employmentsite.oauth2.OAuth2Service;
import univcapstone.employmentsite.token.TokenProvider;

@RestController
@RequiredArgsConstructor
@Slf4j
public class OAuth2Controller {

    private final OAuth2Service oAuth2Service;
    private final TokenProvider tokenProvider;

    @GetMapping("/redirectNaver")
    public ResponseEntity<TokenDto> naverRedirect(HttpServletRequest request, Authentication authentication) {

        return null;
    }

//    @PostMapping("/login/naver")
//    public ResponseEntity<TokenDto> naverLogin(@RequestBody NaverLoginDto naverLoginDto) {
//
//        UserRequestDto socialUser = UserRequestDto.builder()
//                .loginId(naverLoginDto.getLoginId())
//                .name(naverLoginDto.getName())
//                .nickname(naverLoginDto.getNickname())
//                .email(naverLoginDto.getEmail())
//                .build();
//
//        if (userService.findUserByLoginId(socialUser.getLoginId()) == null) {
//            UserResponseDto userResponseDto = authService.join(socialUser);
//            log.info("[{}] success join: {}", userResponseDto.getId(), userResponseDto);
//            TokenDto tokenDto = authService.login(socialUser);
//            log.info("login success");
//            return ResponseEntity.ok(tokenDto);
//        } else {
//            TokenDto tokenDto = authService.login(socialUser);
//            log.info("login success");
//            return ResponseEntity.ok(tokenDto);
//        }
//    }

//    @PostMapping("/login/{provider}")
//    public ResponseEntity<TokenDto> oAuthLogin(@PathVariable String provider, @RequestParam String code) {
//
//
//
//
//
//    }

//    @GetMapping("/delete/oauth2/naver")
//    public ResponseEntity<? extends BasicResponse>

//    @PostMapping("/login/kakao")
//    public ResponseEntity<TokenDto> loginKakao() {
//
//    }

}
