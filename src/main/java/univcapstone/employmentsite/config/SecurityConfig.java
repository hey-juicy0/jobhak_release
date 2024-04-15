package univcapstone.employmentsite.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;
import univcapstone.employmentsite.config.filter.JwtAuthenticationFilter;
import univcapstone.employmentsite.config.filter.JwtExceptionFilter;
import univcapstone.employmentsite.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import univcapstone.employmentsite.oauth2.handler.OAuth2AuthenticationSuccessHandler;
import univcapstone.employmentsite.service.LogoutService;
import univcapstone.employmentsite.oauth2.OAuth2Service;
import univcapstone.employmentsite.token.JwtAuthenticationEntryPoint;
import univcapstone.employmentsite.token.TokenProvider;


@Configuration
@EnableJpaAuditing
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final TokenProvider tokenProvider;
    private final CorsFilter corsFilter;
    private final LogoutService logoutService;
    private final OAuth2Service oAuth2Service;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    // PasswordEncoder는 BCryptPasswordEncoder를 사용
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers("/", "/join", "/joincheck",
                        "/login", "/confirm/**",
                        "/verify/**", "/find/**", "/reissue",
                        "/error", "/favicon.ico", "/redirectNaver");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class) //HTTP 기본 인증 및 CSRF 보안 비활성화, CORS 활성화

                //세션 기반 인증 사용 끔(STATELESS로 설정)
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                //예외 처리 클래스 등록
                .exceptionHandling(requests -> requests
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                )

                .addFilterBefore(
                        new JwtAuthenticationFilter(tokenProvider),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(
                        new JwtExceptionFilter(),
                        JwtAuthenticationFilter.class)

                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                //회원가입, 개인정보 동의, 로그인, 로그아웃, 이메일 인증, 아이디 중복 확인, 아이디 및 비밀번호 찾기, 파비콘
                                .requestMatchers("/", "/join", "/joincheck",
                                        "/login", "/confirm/**",
                                        "/verify/**", "/find/**", "/reissue", "/favicon.ico").permitAll()
                                .anyRequest().authenticated()
                ) //위 URI 외의 URI는 모두 인증 필수

                .formLogin(AbstractHttpConfigurer::disable)
                .logout(logoutConfig -> logoutConfig
                                .logoutUrl("/logout")
                                .addLogoutHandler(logoutService)
//                        .deleteCookies("JSESSIONID")
                                .logoutSuccessHandler((request, response, authentication) ->
                                        log.info("로그아웃 성공")
                                )
                                .permitAll()
                )
                .oauth2Login(oauth2Config ->
                        oauth2Config.authorizationEndpoint(config -> {
                                    config.authorizationRequestRepository(httpCookieOAuth2AuthorizationRequestRepository);
                                })
                                .redirectionEndpoint(config -> config.baseUri("/login/oauth2/code/**"))
                                .userInfoEndpoint(config -> config.userService(oAuth2Service))
                                .successHandler(oAuth2AuthenticationSuccessHandler));

        return httpSecurity.build();
    }
}
