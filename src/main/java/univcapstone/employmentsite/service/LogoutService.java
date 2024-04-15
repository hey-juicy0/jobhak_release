package univcapstone.employmentsite.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import univcapstone.employmentsite.repository.RefreshTokenRepository;
import univcapstone.employmentsite.token.TokenProvider;

@Service
@Slf4j
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenProvider tokenProvider;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
//        //Access Token 유효성 검증
//        String accessToken = tokenProvider.resolveAccessToken(request);

//        if (!tokenProvider.validateToken(accessToken)) {
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            throw new RuntimeException("유효하지 않은 AccessToken 입니다.");
//        }

//        Authentication auth = tokenProvider.getAuthentication(accessToken);
//        String loginId = auth.getName();

        String refreshToken = tokenProvider.getRefreshTokenFromCookies(request);

        log.info("refreshToken = {}", refreshToken);

        String loginId = tokenProvider.getLoginId(refreshToken);

        refreshTokenRepository.deleteById(loginId);
    }
}
