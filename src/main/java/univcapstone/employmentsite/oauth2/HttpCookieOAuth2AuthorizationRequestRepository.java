package univcapstone.employmentsite.oauth2;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import univcapstone.employmentsite.oauth2.utils.CookieUtil;

import java.time.Duration;

//SpringOAuth2는 기본적으로 Session을 사용해 auth req를 저장 -> 그러나 우리는 JWT를 사용하므로 세션에 저장할 필요 없음
@Component
@Slf4j
public class HttpCookieOAuth2AuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {
    public static final String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";
    public static final String REDIRECT_URI_PARAM_COOKIE_NAME = "redirect_uri";
    public static final String MODE_PARAM_COOKIE_NAME = "mode";
    private static final Duration OAUTH_COOKIE_EXPIRY_TIME = Duration.ofMinutes(5);

    // 인증 쿠키 조회
    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        return CookieUtil.getCookie(request, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
                .map(cookie -> CookieUtil.deserialize(cookie, OAuth2AuthorizationRequest.class))
                .orElse(null);
    }

    // 인증 쿠키 저장
    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        if (authorizationRequest == null) {
            CookieUtil.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
            CookieUtil.deleteCookie(request, response, REDIRECT_URI_PARAM_COOKIE_NAME);
            CookieUtil.deleteCookie(request, response, MODE_PARAM_COOKIE_NAME);
            return;
        }

        CookieUtil.addCookie(response,
                OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME,
                CookieUtil.serialize(authorizationRequest),
                OAUTH_COOKIE_EXPIRY_TIME);

        String redirectUriAfterLogin = request.getParameter(REDIRECT_URI_PARAM_COOKIE_NAME);

        log.info("redirectUriAfterLogin = {}", redirectUriAfterLogin);
        if (StringUtils.hasText(redirectUriAfterLogin)) {
            CookieUtil.addCookie(response,
                    REDIRECT_URI_PARAM_COOKIE_NAME,
                    redirectUriAfterLogin,
                    OAUTH_COOKIE_EXPIRY_TIME);
        }

        String mode = request.getParameter(MODE_PARAM_COOKIE_NAME);
        if (StringUtils.hasText(mode)) {
            CookieUtil.addCookie(response,
                    MODE_PARAM_COOKIE_NAME,
                    mode,
                    OAUTH_COOKIE_EXPIRY_TIME);
        }
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
        OAuth2AuthorizationRequest oAuth2AuthorizationRequest = getCookie(request);
        CookieUtil.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
        return oAuth2AuthorizationRequest;
    }

    private OAuth2AuthorizationRequest getCookie(HttpServletRequest request) {
        return CookieUtil.getCookie(request, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
                .map(cookie -> CookieUtil.deserialize(cookie, OAuth2AuthorizationRequest.class))
                .orElse(null);
    }

    public void removeAuthorizationRequestCookies(HttpServletRequest request, HttpServletResponse response) {
        CookieUtil.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
        CookieUtil.deleteCookie(request, response, REDIRECT_URI_PARAM_COOKIE_NAME);
        CookieUtil.deleteCookie(request, response, MODE_PARAM_COOKIE_NAME);
    }

}
