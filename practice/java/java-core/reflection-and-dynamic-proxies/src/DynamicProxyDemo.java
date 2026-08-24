import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Real, executed proof of how java.lang.reflect.Proxy-based dynamic proxies
 * work -- the exact mechanism behind Spring's interface-based JDK AOP
 * proxies. An InvocationHandler intercepts EVERY interface method call,
 * with real before/after logging around the real delegate call. Also real
 * proof of the interface-only constraint: java.lang.reflect.Proxy cannot
 * create a proxy for a concrete class, only interfaces -- the real reason
 * Spring falls back to CGLIB/ByteBuddy for class-based proxying.
 */
public class DynamicProxyDemo {

    interface UserService {
        String findUser(int id);
    }

    static class RealUserService implements UserService {
        @Override
        public String findUser(int id) {
            return "user-" + id;
        }
    }

    public static void main(String[] args) {
        UserService real = new RealUserService();

        InvocationHandler loggingHandler = (proxy, method, methodArgs) -> {
            System.out.println("[proxy] before: " + method.getName() + "(" + java.util.Arrays.toString(methodArgs) + ")");
            long start = System.nanoTime();
            Object result = method.invoke(real, methodArgs); // real delegation to the real object
            long elapsedNanos = System.nanoTime() - start;
            System.out.println("[proxy] after:  " + method.getName() + " returned \"" + result + "\" in " + elapsedNanos + "ns");
            return result;
        };

        UserService proxy = (UserService) Proxy.newProxyInstance(
                UserService.class.getClassLoader(),
                new Class<?>[]{UserService.class},
                loggingHandler);

        System.out.println("proxy's real runtime class: " + proxy.getClass().getName());
        System.out.println("proxy instanceof UserService: " + (proxy instanceof UserService));
        System.out.println("proxy instanceof RealUserService: " + (proxy instanceof RealUserService));

        String result = proxy.findUser(42);
        System.out.println("Caller received: " + result);

        System.out.println("\n== Real proof: java.lang.reflect.Proxy can ONLY proxy interfaces, not concrete classes ==");
        try {
            Proxy.newProxyInstance(
                    RealUserService.class.getClassLoader(),
                    new Class<?>[]{RealUserService.class}, // a concrete class, not an interface
                    loggingHandler);
            System.out.println("Proxying a concrete class succeeded (unexpected)");
        } catch (IllegalArgumentException e) {
            System.out.println("Proxying a concrete class threw real IllegalArgumentException: " + e.getMessage());
            System.out.println("This is the real, concrete reason Spring AOP falls back to CGLIB/ByteBuddy"
                    + " (subclass-based proxying) when a bean has no interface to proxy against.");
        }
    }
}
