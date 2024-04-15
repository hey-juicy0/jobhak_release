package univcapstone.employmentsite.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

import static univcapstone.employmentsite.util.AuthConstants.AUTH_HEADER;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true); //기본적으로 요청에 대한 응답으로 JSON 형식이 나간다.
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("*"));

        config.addExposedHeader(AUTH_HEADER);
//        config.addExposedHeader(REFRESH_HEADER);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

}
