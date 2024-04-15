package univcapstone.employmentsite.oauth2;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import static univcapstone.employmentsite.util.AuthConstants.BEARER_PREFIX;

@RequiredArgsConstructor
@Component
public class NaverOAuth2Unlink implements OAuth2Unlink {

    private static final String URL = "https://nid.naver.com/oauth2.0/token";
    private final RestTemplate restTemplate;

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String clientSecret;

    private static final String VALID_URL = "https://openapi.naver.com/v1/nid/me";

    @Override
    public void unlink(String accessToken) {

        //연동 해제를 수행하기 전에 접근토큰의 유효성을 점검?
//        NaverUtil.validAccessToken();

        //접근 토큰 삭제 시 service_provider, access_token 필수
        String url = URL +
                "?service_provider='NAVER'" +
                "&grant_type=delete" +
                "&client_id=" +
                clientId +
                "&client_secret=" +
                clientSecret +
                "&access_token=" +
                accessToken;

        UnlinkResponse response = restTemplate.getForObject(url, UnlinkResponse.class);

        if (response == null) { // !response.getResult().equals("success")
            throw new RuntimeException("Failed to Naver Unlink");
        }
    }

    @Getter
    @RequiredArgsConstructor
    private static class UnlinkResponse {
        // JsonProperty 어노테이션은 네이버에서 access_token(스네이크 케이스)으로 응답하기 때문에
        // 이를 매핑 시켜주려고 사용하는 어노테이션이다.
        @JsonProperty("access_token")
        private final String accessToken;
        private final String result;
    }

    public void validAccessToken(HttpServletRequest request, String accessToken) throws JsonProcessingException {

        String authorizationHeader = BEARER_PREFIX + accessToken;

        ResponseEntity<ValidAccessTokenResponse> responseEntity = restTemplate.exchange(
                VALID_URL,
                HttpMethod.GET,
                new HttpEntity<>(authorizationHeader),
                ValidAccessTokenResponse.class
        );

        ObjectMapper mapper = new ObjectMapper();
        String response = mapper.writeValueAsString(responseEntity.getBody());

        ValidAccessTokenResponse result = mapper.readValue(response, ValidAccessTokenResponse.class);

//        if (result )
    }

    //리프레시 토큰을 이용하여 갱신

    @Getter
    @RequiredArgsConstructor
    private static class ValidAccessTokenResponse {
        private final String resultcode;
        private final String message;
    }
}
