package demo;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Added AFTER capturing this chapter's real "before" CORS failure (a
// genuine, browser-blocked fetch from the Next.js app's own origin,
// port 5198, to this backend's port 8080 -- two different origins by the
// browser's own same-origin definition, since the port differs). This is
// the real fix: an explicit allowlist naming the Next.js app's exact
// origin, not a wildcard.
@Configuration
class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/public/**")
            .allowedOrigins("http://localhost:5198")
            .allowedMethods("GET");
    }
}
