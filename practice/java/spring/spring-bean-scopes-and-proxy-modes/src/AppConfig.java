import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.context.support.SimpleThreadScope;

@Configuration
public class AppConfig {

    // Registers a custom "thread" scope. Must be a STATIC @Bean method: this is a
    // BeanFactoryPostProcessor, and Spring requires those to be instantiated before
    // any other bean, which static @Bean factory methods guarantee.
    @Bean
    public static CustomScopeConfigurer customScopeConfigurer() {
        CustomScopeConfigurer configurer = new CustomScopeConfigurer();
        configurer.addScope("thread", new SimpleThreadScope());
        return configurer;
    }

    // Plain prototype -- no proxy. Injecting this directly into a singleton
    // (see SingletonHolder) reproduces the classic "prototype captured once" bug.
    @Bean("greeter")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Greeter greeter() {
        return new Greeter();
    }

    // Prototype + scoped proxy. Injecting THIS into a singleton (see
    // ScopedProxyHolder) is the real, correct fix: every call is re-dispatched
    // through the proxy to a fresh prototype instance.
    @Bean("proxiedGreeter")
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public Greeter proxiedGreeter() {
        return new Greeter();
    }

    // Custom "thread" scope: one instance per thread, reused across calls on the
    // same thread -- the same registry-lookup mechanism request/session scope use
    // in a web container, without needing a servlet container to demonstrate it.
    @Bean
    @Scope("thread")
    public Greeter threadScopedGreeter() {
        return new Greeter();
    }

    @Bean
    public SingletonHolder singletonHolder(@Qualifier("greeter") Greeter greeter) {
        return new SingletonHolder(greeter);
    }

    @Bean
    public ScopedProxyHolder scopedProxyHolder(@Qualifier("proxiedGreeter") Greeter proxiedGreeter) {
        return new ScopedProxyHolder(proxiedGreeter);
    }

    @Bean
    public ObjectProviderHolder objectProviderHolder(@Qualifier("greeter") ObjectProvider<Greeter> greeterProvider) {
        return new ObjectProviderHolder(greeterProvider);
    }
}
