package univcapstone.employmentsite.service;

import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import univcapstone.employmentsite.domain.Authority;
import univcapstone.employmentsite.domain.RefreshToken;
import univcapstone.employmentsite.domain.User;
import univcapstone.employmentsite.dto.TokenDto;
import univcapstone.employmentsite.dto.UserRequestDto;
import univcapstone.employmentsite.dto.UserResponseDto;
import univcapstone.employmentsite.repository.RefreshTokenRepository;
import univcapstone.employmentsite.repository.UserRepository;
import univcapstone.employmentsite.token.TokenProvider;

import static univcapstone.employmentsite.util.AuthConstants.*;


@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder; //인증 객체를 생성해주는 빌더
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public UserResponseDto join(UserRequestDto userRequestDto) {

        if (userRepository.existsByLoginId(userRequestDto.getLoginId())) {
            throw new RuntimeException("사용할 수 없는 아이디입니다. 다른 아이디를 입력해 주세요.");
        }

        User user = User.builder()
                .loginId(userRequestDto.getLoginId())
                .password(passwordEncoder.encode(userRequestDto.getPassword()))
                .nickname(userRequestDto.getNickname())
                .email(userRequestDto.getEmail())
                .name(userRequestDto.getName())
                .authority(Authority.USER)
                .build();

        return new UserResponseDto(userRepository.save(user));
    }

    public TokenDto login(UserRequestDto userRequestDto) {

        //요청으로 넘어온 로그인 아이디와 비밀번호를 통해 인증 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userRequestDto.getLoginId(), userRequestDto.getPassword());

        //비밀번호가 일치하는지 검증
        //authenticate 메서드가 실행될 때 CustomUserDetailsService에 만든 loadUserByUsername 메서드가 실행된다.
        //여기서 일치하지 않으면 AuthenticationEntryPoint로 간다.
        Authentication authenticate = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        //Authenticate에 저장된 User의 loginId로 user를 얻어온다.
        User user = userRepository.findByLoginId(authenticate.getName())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다"));

        //해당 user를 기반으로 JWT TOKEN 생성
        String accessToken = tokenProvider.createAccessToken(authenticate);
        String refreshToken = tokenProvider.createRefreshToken(authenticate);

        return TokenDto.builder()
                .grantType(BEARER_PREFIX)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * JWT는 한 번 발급하면 만료되기 전까지 삭제할 수 없다.
     * 따라서 짧은 유효시간을 갖는 Access Token과 (접근에 관여하는 토큰)
     * 저장소에 저장해서 Access Token을 재발급이 가능한 Refresh Token이 있다. (재발급에 관여하는 토큰)
     * @param refreshToken
     * @return
     */
    public TokenDto reissue(String refreshToken, Authentication authentication) {

        //RefreshToken 유효성 검증 (RefreshToken의 TTL로 인해 refreshToken이 만료되면 데이터가 자동 삭제됨)
        RefreshToken findRefreshToken = refreshTokenRepository.findRefreshTokenByRefreshToken(refreshToken)
                .orElseThrow(() -> new MalformedJwtException("만료된 Refresh Token 입니다. 다시 로그인 하세요."));

        User user = userRepository.findByLoginId(findRefreshToken.getLoginId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다.")); //해당 리프레쉬 토큰의 유저를 찾는다.

        //Access Token 재발급 진행
        String newAccessToken = tokenProvider.createAccessToken(authentication);

        log.info("재발급된 Access Token = {}", newAccessToken);

        return TokenDto.builder()
                .grantType(BEARER_PREFIX)
                .accessToken(newAccessToken)
                .refreshToken(findRefreshToken.getRefreshToken())
                .build();
    }

    public void deleteRefreshToken(String loginId) {
        refreshTokenRepository.deleteById(loginId);
    }

}
