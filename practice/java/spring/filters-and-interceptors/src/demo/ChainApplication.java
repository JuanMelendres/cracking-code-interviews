package demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Real Spring Boot 3.5.16 app on embedded Tomcat, port 8080.
 *
 * <p>Registers two Servlet filters and two Spring MVC interceptors so the
 * transcript shows both the nesting order and the reverse unwinding order,
 * rather than a single layer that cannot distinguish the two.
 */
@SpringBootApplication
public class ChainApplication implements WebMvcConfigurer {

    public static void main(String[] args) {
        SpringApplication.run(ChainApplication.class, args);
    }

    @Bean
    FilterRegistrationBean<TraceFilter> outerFilter() {
        FilterRegistrationBean<TraceFilter> registration =
                new FilterRegistrationBean<>(new TraceFilter("outer-filter", null));
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }

    @Bean
    FilterRegistrationBean<TraceFilter> innerFilter() {
        FilterRegistrationBean<TraceFilter> registration =
                new FilterRegistrationBean<>(new TraceFilter("inner-filter", "/boom-filter"));
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
        return registration;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TraceInterceptor("first-interceptor", null));
        registry.addInterceptor(new TraceInterceptor("second-interceptor", "/blocked"));
    }
}
