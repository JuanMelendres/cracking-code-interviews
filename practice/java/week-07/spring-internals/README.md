# Week 7 Java — Spring Internals — runnable verification

Two real demos, plain jars from Maven Central (no Spring Boot/Maven), same approach as Week 3.

## Setup

```bash
cd practice/java/week-07/spring-internals
./fetch-deps.sh
mkdir -p out
javac -cp "lib/*" -d out src/*.java
```

## 1. Bean lifecycle order — `BeanLifecycleDemo.java`

```bash
java -cp "out:lib/*" BeanLifecycleDemo
```

**Real observed output (last run):**

```
1. Spring context refresh begins
2. bean definition registered
3. constructor
4. BeanPostProcessor.postProcessBeforeInitialization
5. @PostConstruct
6. InitializingBean.afterPropertiesSet()
7. custom init-method (from @Bean(initMethod=...))
8. BeanPostProcessor.postProcessAfterInitialization
(context fully refreshed -- bean is now ready for use)
9. @PreDestroy
10. DisposableBean.destroy()
11. custom destroy-method (from @Bean(destroyMethod=...))
```

## 2. `@Async` + `@Transactional` on the same method — `AsyncTransactionalDemo.java`

```bash
java -cp "out:lib/*" AsyncTransactionalDemo
```

**Real observed output (last run):**

```
Calling the @Async @Transactional method...
Call returned after 12ms. Exception visible to caller: false
(At this point the caller has NO idea whether the operation succeeded, failed, or is still running on the async executor thread.)

[test observer, not something the real caller could see] async work actually completed: true
GRAVE: Unexpected exception occurred invoking async method: ...
java.lang.RuntimeException: simulated failure INSIDE the async+transactional method
    ...
[test observer] row count after the exception: 0 (0 means the transaction correctly rolled back, even though it ran on a different thread)
```

**What this proves:** the transaction itself still works correctly (row count 0 confirms the rollback happened, on the executor thread). The "unexpected behavior" is that a `void @Async` method returns to its caller in 12ms, before the transactional work even runs — the exception is never visible to the caller at all, only logged by Spring's default uncaught-exception handler. The caller has no way to know the operation failed unless it returns a `Future`/`CompletableFuture` and calls `.get()`.
