package univcapstone.employmentsite.oauth2;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OAuth2Provider {

    NAVER("naver"),
    KAKAO("kakao"),
    JOBHAK("jobhak");

    private final String provider;

}
