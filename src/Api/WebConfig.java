package Api;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String configuredOrigins = System.getenv("APP_CORS_ORIGINS");
        String[] origins = configuredOrigins == null || configuredOrigins.isBlank()
                ? new String[]{"http://localhost:*", "http://127.0.0.1:*", "https://localhost:*", "capacitor://localhost"}
                : configuredOrigins.split(",");
        registry.addMapping("/api/**")
                .allowedOriginPatterns(origins)
                .allowedMethods("GET", "POST", "PUT", "OPTIONS")
                .allowedHeaders("Authorization", "Content-Type", "Accept")
                .allowCredentials(false)
                .maxAge(3600);
    }
}
