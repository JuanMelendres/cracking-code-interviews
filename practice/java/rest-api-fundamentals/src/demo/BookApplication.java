package demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

// T-2205: a real, minimal REST API demonstrating resource naming, correct
// HTTP verb-to-operation mapping, and correct status codes -- separate from
// T-2203's own Spring MVC Fundamentals demo (a different, focused set of
// claims: this one is about REST conventions, not Spring's DI mechanism).
@SpringBootApplication
public class BookApplication {
    public static void main(String[] args) {
        SpringApplication.run(BookApplication.class,
            "--server.port=8081",
            "--logging.level.root=WARN"
        );
    }

    // A real, built-in Spring Framework filter -- computes an ETag (an MD5
    // hash of the response body) on every response and, when a request
    // carries a matching If-None-Match header, replaces the real 200
    // response with a real, empty-bodied 304 automatically. Zero hand-rolled
    // hashing/comparison code; this is what "conditional GET" means in
    // practice for a real Spring app.
    @Bean
    public FilterRegistrationBean<ShallowEtagHeaderFilter> etagFilter() {
        FilterRegistrationBean<ShallowEtagHeaderFilter> registration =
                new FilterRegistrationBean<>(new ShallowEtagHeaderFilter());
        registration.addUrlPatterns("/books/*");
        return registration;
    }
}
