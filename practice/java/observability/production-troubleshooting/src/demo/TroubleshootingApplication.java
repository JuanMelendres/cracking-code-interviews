package demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;

/**
 * Real Spring Boot 3.5.16 app on embedded Tomcat, port 8080.
 *
 * <p>Run it twice to reproduce the transcript: once at the default log level,
 * and once with {@code --logging.level.demo=DEBUG}. The difference between the
 * two runs is the whole point of the "wrong level" cause.
 */
@SpringBootApplication
public class TroubleshootingApplication {

    public static void main(String[] args) {
        SpringApplication.run(TroubleshootingApplication.class, args);
    }

    @Bean
    FilterRegistrationBean<AccessLogFilter> accessLog() {
        FilterRegistrationBean<AccessLogFilter> registration =
                new FilterRegistrationBean<>(new AccessLogFilter());
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }
}
