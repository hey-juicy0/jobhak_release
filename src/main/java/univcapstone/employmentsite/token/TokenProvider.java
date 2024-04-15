package univcapstone.employmentsite.token;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import univcapstone.employmentsite.domain.Authority;
import univcapstone.employmentsite.domain.RefreshToken;
import univcapstone.employmentsite.repository.RefreshTokenRepository;

import java.security.Key;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static univcapstone.employmentsite.util.AuthConstants.*;

@Component
@Slf4j
public class TokenProvider implements InitializingBean {

    private final String secret;
    private Key key;
    private final CustomUserDetailsService customUserDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;

    public TokenProvider(
            @Value("${jwt.secret}") String secret,
            CustomUserDetailsService customUserDetailsService, RefreshTokenRepository refreshTokenRepository) {
        this.secret = secret;
        this.customUserDetailsService = customUserDetailsService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    // 빈이 생성되고 주입을 받은 후에 secret 값을 Base64 Decode해서 key 변수에 할당하기 위해
    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    //JWT 토큰 생성하는 메서드
//    public TokenDto createToken(Authentication authentication) {
//        Date date = new Date();
//
//        //Access Token 생성
//        String accessToken = createToken(authentication, ACCESS_TOKEN_VALID_TIME);
//
//        //Refresh Token 생성
//        String refreshToken = createToken(authentication, REFRESH_TOKEN_VALID_TIME);
//
//        return TokenDto.builder()
//                .grantType(BEARER_PREFIX)
//                .accessToken(accessToken)
//                .refreshToken(refreshToken)
//                .build();
//    }

    public String createAccessToken(Authentication authentication) {
        return createToken(authentication);
    }
//
    public String createRefreshToken(Authentication authentication) {
        String refreshToken = createToken(authentication);

        //RefreshToken 저장
        RefreshToken storedRefreshToken = RefreshToken.builder()
                .refreshToken(refreshToken)
                .loginId(authentication.getName())
                .build();

        refreshTokenRepository.save(storedRefreshToken);

        return refreshToken;
    }

    private String createToken(Authentication authentication) {
        Claims claims = Jwts.claims().setSubject(authentication.getName()); //토큰 제목 정보 -> 여기에 loginId 들어간다.
        Date date = new Date();
        Date expiredDate = new Date(date.getTime() + REFRESH_TOKEN_VALID_TIME);

        return Jwts.builder()
//                .setSubject(authentication.getName())
                .setClaims(claims)
                .claim(AUTHORITIES_KEY, Collections.singleton(new SimpleGrantedAuthority(Authority.USER.getRole()))) // 정보 저장
                .setIssuedAt(date)
                .setExpiration(expiredDate) //토큰 만료 시간 (해당 옵션 안 넣으면 만료 안 함)
                .signWith(key, SignatureAlgorithm.HS512) // 사용할 암호화 알고리즘과 , signature 에 들어갈 secret값 세팅
                .compact();
    }


    //반대로 토큰을 받아서 토큰에 담겨 있는 권한 정보들을 이용해서 Authentication 객체를 리턴하는 메서드
    public Authentication getAuthentication(String accessToken) {
        //토큰 복호화
        Claims claims = parseClaims(accessToken); //토큰에 담겨 있는 권한 정보

        if (claims.get(AUTHORITIES_KEY) == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(claims.getSubject());

        return new UsernamePasswordAuthenticationToken(userDetails, accessToken,
                Collections.singleton(new SimpleGrantedAuthority(Authority.USER.getRole()))); //유저 객체와 토큰을 통해 Authentication 객체 리턴
    }

    //토큰의 유효성 검증을 수행
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getLoginId(String token) {
        return parseClaims(token).getSubject();
    }

    public String getRefreshTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(REFRESH_COOKIE_NAME))
                .findAny()
                .orElseThrow(() -> new RuntimeException("Refresh Token이 존재하지 않습니다."))
                .getValue();
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        } catch (SecurityException e) {
            log.info("Invalid JWT signature.");
            throw new SecurityException("잘못된 JWT 시그니처입니다.");
        } catch (MalformedJwtException e) {
            log.info("Invalid JWT token.");
            throw new MalformedJwtException("유효하지 않은 JWT 토큰입니다.");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.");
            throw new ExpiredJwtException(e.getHeader(), e.getClaims(), "토큰 기한이 만료됐습니다.");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token.");
            throw new UnsupportedJwtException("지원하지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT token compact of handler are invalid.");
            throw new IllegalArgumentException("JWT token compact of handler are invalid.");
        }
    }

    public void setAccessTokenHeader(String accessToken, HttpServletResponse response) {
        String headerValue = BEARER_PREFIX + accessToken;
        response.setHeader(AUTH_HEADER, headerValue);
    }

//    public void refresshTokenSetHeader(String refreshToken, HttpServletResponse response) {
//        response.setHeader("Refresh", refreshToken);
//    }

    // Request Header에 Access Token 정보를 추출하는 메서드
    public String resolveAccessToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTH_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // Request Header에 Refresh Token 정보를 추출하는 메서드
//    public String resolveRefreshToken(HttpServletRequest request) {
//        String bearerToken = request.getHeader(REFRESH_HEADER);
//        if (StringUtils.hasText(bearerToken)) {
//            return bearerToken;
//        }
//        return null;
//    }
}