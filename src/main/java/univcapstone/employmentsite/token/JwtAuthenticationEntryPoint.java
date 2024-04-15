package univcapstone.employmentsite.token;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import univcapstone.employmentsite.util.response.ErrorResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 인증이 안된 사용자가 인증이 필요한 URI에 접근하게 되면 보낼 응답 설정
 */
@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.error("Not Authenticated Request", authException);
        log.error("Request Uri : {}", request.getRequestURI());

        final ObjectMapper mapper = new ObjectMapper();

        ErrorResponse errorResponse = new ErrorResponse(request.getServletPath(), HttpStatus.UNAUTHORIZED.value(), authException.getMessage());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        mapper.writeValue(response.getWriter(), errorResponse);
    }
}
