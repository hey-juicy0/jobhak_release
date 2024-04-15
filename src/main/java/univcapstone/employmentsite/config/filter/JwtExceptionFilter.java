package univcapstone.employmentsite.config.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.filter.OncePerRequestFilter;
import univcapstone.employmentsite.util.response.ErrorResponse;

import java.io.IOException;

/**
 * JWT 관련 오류 처리(인증 오류 X)
 */
@Slf4j
public class JwtExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (JwtException e) {
            setErrorResponse(request, response, e);
        }
    }

    private void setErrorResponse(HttpServletRequest request, HttpServletResponse response, JwtException exception) throws RuntimeException, IOException {

        final ObjectMapper mapper = new ObjectMapper();
        ErrorResponse errorResponse;

        //토큰 기한 만료 코드 999
        if (exception instanceof ExpiredJwtException) {
            errorResponse = new ErrorResponse(request.getServletPath(),
                    999,
                    exception.getMessage());
        } else {
            errorResponse = new ErrorResponse(request.getServletPath(),
                    response.getStatus(),
                    exception.getMessage());
        }


        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        mapper.writeValue(response.getWriter(), errorResponse);
    }
}
