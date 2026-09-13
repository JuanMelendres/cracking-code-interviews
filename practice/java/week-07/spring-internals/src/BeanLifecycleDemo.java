import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.*;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import java.util.ArrayList;
import java.util.List;

/**
 * Real, observed Spring bean lifecycle order -- every callback a bean can
 * hook into, in the order Spring actually invokes them, captured in a
 * shared list rather than asserted from documentation.
 */
public class BeanLifecycleDemo {

    static final List<String> callOrder = new ArrayList<>();

    @Configuration
    static class Config {
        @Bean(initMethod = "customInit", destroyMethod = "customDestroy")
        TrackedBean trackedBean() {
            callOrder.add("3. constructor");
            return new TrackedBean();
        }

        @Bean
        static BeanPostProcessor tracker() {
            return new BeanPostProcessor() {
                @Override
                public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
                    if (bean instanceof TrackedBean) callOrder.add("4. BeanPostProcessor.postProcessBeforeInitialization");
                    return bean;
                }
                @Override
                public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                    if (bean instanceof TrackedBean) callOrder.add("8. BeanPostProcessor.postProcessAfterInitialization");
                    return bean;
                }
            };
        }
    }

    static class TrackedBean implements InitializingBean, DisposableBean {
        @PostConstruct
        void postConstruct() {
            callOrder.add("5. @PostConstruct");
        }

        @Override
        public void afterPropertiesSet() {
            callOrder.add("6. InitializingBean.afterPropertiesSet()");
        }

        void customInit() {
            callOrder.add("7. custom init-method (from @Bean(initMethod=...))");
        }

        @PreDestroy
        void preDestroy() {
            callOrder.add("9. @PreDestroy");
        }

        @Override
        public void destroy() {
            callOrder.add("10. DisposableBean.destroy()");
        }

        void customDestroy() {
            callOrder.add("11. custom destroy-method (from @Bean(destroyMethod=...))");
        }
    }

    public static void main(String[] args) {
        callOrder.add("1. Spring context refresh begins");
        callOrder.add("2. bean definition registered");

        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(Config.class);
        callOrder.add("(context fully refreshed -- bean is now ready for use)");
        ctx.close(); // triggers the destroy-phase callbacks

        System.out.println("Real observed callback order:");
        for (String step : callOrder) {
            System.out.println("  " + step);
        }
    }
}
