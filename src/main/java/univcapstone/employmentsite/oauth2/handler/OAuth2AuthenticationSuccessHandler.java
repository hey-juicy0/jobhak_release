package univcapstone.employmentsite.oauth2.handler;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import univcapstone.employmentsite.domain.Authority;
import univcapstone.employmentsite.domain.RefreshToken;
import univcapstone.employmentsite.domain.User;
import univcapstone.employmentsite.oauth2.*;
import univcapstone.employmentsite.oauth2.utils.CookieUtil;
import univcapstone.employmentsite.repository.RefreshTokenRepository;
import univcapstone.employmentsite.repository.UserRepository;
import univcapstone.employmentsite.token.TokenProvider;

import java.io.IOException;
import java.util.Optional;

import static univcapstone.employmentsite.oauth2.HttpCookieOAuth2AuthorizationRequestRepository.MODE_PARAM_COOKIE_NAME;

@RequiredArgsConstructor
@Slf4j
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;
    private final OAuth2UnlinkManager oAuth2UserUnlinkManager;
    private final String REDIRECT_URI = "https://localhost:3000/login/callback";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        String targetUrl;

        targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {

        String mode = CookieUtil.getCookie(request, MODE_PARAM_COOKIE_NAME)
                .map(Cookie::getValue)
                .orElse("");

        CustomOAuth2User customOAuth2User = getOAuth2UserPrincipal(authentication);


        if (customOAuth2User == null) {
            return UriComponentsBuilder.fromUriString(REDIRECT_URI)
                    .queryParam("error", "Login failed")
                    .build().toUriString();
        }

        Optional<User> optionalUser = userRepository.findByEmail(customOAuth2User.getUsername());

        if ("login".equalsIgnoreCase(mode)) {
            // TODO: DB 저장
            // TODO: 액세스 토큰, 리프레시 토큰 발급
            // TODO: 리프레시 토큰 DB 저장
            OAuth2UserInfo userInfo = customOAuth2User.getUserInfo();

            log.info("email={}, name={}, nickname={}, accessToken={}", customOAuth2User.getUserInfo().getEmail(),
                    userInfo.getName(),
                    userInfo.getNickname(),
                    userInfo.getAccessToken()
            );

            //첫 회원가입 시에만 DB 저장
            if (optionalUser.isEmpty()) {
                User user = User.builder()
                        .loginId(userInfo.getEmail())
                        .email(userInfo.getEmail())
                        .name(userInfo.getName())
                        .authority(Authority.USER)
                        .nickname(userInfo.getNickname()).build();

                userRepository.save(user);
            }

            String accessToken = tokenProvider.createAccessToken(authentication);
            String refreshToken = tokenProvider.createRefreshToken(authentication);

            return UriComponentsBuilder.fromUriString(REDIRECT_URI)
                    .queryParam("accessToken", accessToken)
                    .queryParam("refreshToken", refreshToken)
                    .build().toUriString();

        } else if ("unlink".equalsIgnoreCase(mode)) {

            String accessToken = customOAuth2User.getUserInfo().getAccessToken();
            OAuth2Provider provider = customOAuth2User.getUserInfo().getProvider();

            OAuth2UserInfo oAuth2UserInfo = (OAuth2UserInfo) tokenProvider.getAuthentication(accessToken).getPrincipal();

            User user = userRepository.findByEmail(oAuth2UserInfo.getEmail())
                    .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다."));

            // TODO: DB 삭제
            userRepository.delete(user);

            // TODO: 리프레시 토큰 삭제
            String strRefreshToken = tokenProvider.getRefreshTokenFromCookies(request);
            RefreshToken refreshToken = refreshTokenRepository.findRefreshTokenByRefreshToken(strRefreshToken)
                    .orElseThrow(() -> new RuntimeException("Refresh Token이 존재하지 않습니다."));

            refreshTokenRepository.delete(refreshToken);

            oAuth2UserUnlinkManager.unlink(provider, accessToken);

            return UriComponentsBuilder.fromUriString(REDIRECT_URI)
                    .build().toUriString();
        }

        return UriComponentsBuilder.fromUriString(REDIRECT_URI)
                .queryParam("error", "Login failed")
                .build().toUriString();
    }

    private CustomOAuth2User getOAuth2UserPrincipal(Authentication authentication) {
        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomOAuth2User) {
            return (CustomOAuth2User) principal;
        }
        return null;
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    //성공 시 DB에 저장한다.
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//
//        TokenDto tokenDto = generateTokenDto(response, authentication);
//
//        String result = mapper.writeValueAsString(tokenDto);
//
//        response.getWriter().write(result);
//
//        CustomOAuth2User customOAuth2User = getOAuth2UserPrincipal(authentication);
//
//        if (customOAuth2User == null) {
//            //에러처리
//        }
//
//        OAuth2UserInfo userInfo = customOAuth2User.getUserInfo();
//
//        User user = User.builder()
//                .loginId(userInfo.getId())
//                .name(userInfo.getName())
//                .nickname(userInfo.getNickname()).build();
//
//        userRepository.save(user);
//
//        // 토큰 전달을 위한 redirect
//        String redirectUrl = UriComponentsBuilder.fromUriString("https://localhost:3000/redirectNaver")
//                .buildAndExpand(tokenDto)
//                .toUriString();
//
//        response.sendRedirect(redirectUrl);
//
//        userRepository.save(user);
//
////        userRepository.findByEmail(email);
//
//        //(카카오만) userRepository에서 회원이 있으면 바로 홈
//        //없으면 changeName
//
//        log.info("[oauth Provider에서 제공한 access token = {}]", userInfo.getAccessToken());
//        log.info("[자체 사이트의 access token = {}, refresh token = {}]", tokenDto.getAccessToken(), tokenDto.getRefreshToken());
//    }
//
//    private TokenDto generateTokenDto(HttpServletResponse response, Authentication authentication) {
//
//        String accessToken = tokenProvider.createAccessToken(authentication);
//        String refreshToken = tokenProvider.createRefreshToken(authentication);
//
//        tokenProvider.setAccessTokenHeader(accessToken, response);
//
//        return TokenDto.builder()
//                .grantType(BEARER_PREFIX)
//                .accessToken(accessToken)
//                .refreshToken(refreshToken)
//                .build();
//    }
//
//    private CustomOAuth2User getOAuth2UserPrincipal(Authentication authentication) {
//        Object principal = authentication.getPrincipal();
//
//        if (principal instanceof CustomOAuth2User) {
//            return (CustomOAuth2User) principal;
//        }
//        return null;
//    }
}
