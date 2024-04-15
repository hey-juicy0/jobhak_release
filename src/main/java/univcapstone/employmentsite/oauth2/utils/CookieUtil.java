package univcapstone.employmentsite.oauth2.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.SerializationUtils;

import java.time.Duration;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

public class CookieUtil {

    public static Optional<Cookie> getCookie(HttpServletRequest request, String cookieName) {
        return Optional.ofNullable(request.getCookies())
                .flatMap(cookies -> Arrays.stream(cookies)
                        .filter(cookie -> cookie.getName().equals(cookieName))
                        .findAny());
    }

    public static void addCookie(HttpServletResponse response, String name, String value, Duration maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge((int) maxAge.toSeconds());
        response.addCookie(cookie);
    }

    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String cookieName) {
        Optional.of(request.getCookies())
                .ifPresent(cookies ->
                        Arrays.stream(cookies)
                                .filter(cookie -> cookie.getName().equals(cookieName))
                                .forEach(cookie -> {
                                    cookie.setValue("");
                                    cookie.setPath("/");
                                    cookie.setMaxAge(0);
                                    response.addCookie(cookie);
                                })
                );
    }

    public static String serialize(Object object) {
        return Base64.getUrlEncoder()
                .encodeToString(SerializationUtils.serialize(object));
    }

    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        return cls.cast(SerializationUtils.deserialize(
                Base64.getUrlDecoder().decode(cookie.getValue())));
    }

}
