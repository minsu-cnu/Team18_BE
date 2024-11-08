package team18.team18_be.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

  @Value("${front.origin}")
  private String FRONT_ORIGIN;

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/api/**")
        .allowedOrigins(FRONT_ORIGIN)
        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD")
        .allowedHeaders("Authorization", "Content-Type")
        .exposedHeaders(HttpHeaders.LOCATION, HttpHeaders.AUTHORIZATION)
        .allowCredentials(true)
        .maxAge(1800);
  }
}
