import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;

/**
 * A real Hibernate {@link ConnectionProvider} that wraps every {@code
 * PreparedStatement} it hands out in a JDK dynamic proxy, incrementing
 * {@link BatchCallCounter} on every real {@code executeBatch()} and
 * {@code executeUpdate()} call. This is the mechanism this pack uses to
 * prove -- not assume -- whether Hibernate is genuinely batching inserts.
 */
final class CountingConnectionProvider implements ConnectionProvider {

    private final String url;

    CountingConnectionProvider(String url) {
        this.url = url;
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection real = DriverManager.getConnection(url, "sa", "");
        return (Connection) Proxy.newProxyInstance(
                Connection.class.getClassLoader(),
                new Class<?>[]{Connection.class},
                new CountingInvocationHandler(real));
    }

    @Override
    public void closeConnection(Connection conn) throws SQLException {
        conn.close();
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

    @Override
    public boolean isUnwrappableAs(Class<?> unwrapType) {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> unwrapType) {
        throw new UnsupportedOperationException();
    }

    private static final class CountingInvocationHandler implements InvocationHandler {
        private final Connection real;

        CountingInvocationHandler(Connection real) {
            this.real = real;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Object result = method.invoke(real, args);
            if ("prepareStatement".equals(method.getName()) && result instanceof PreparedStatement) {
                return Proxy.newProxyInstance(
                        PreparedStatement.class.getClassLoader(),
                        new Class<?>[]{PreparedStatement.class},
                        new StatementCountingHandler((PreparedStatement) result));
            }
            return result;
        }
    }

    private static final class StatementCountingHandler implements InvocationHandler {
        private final PreparedStatement real;

        StatementCountingHandler(PreparedStatement real) {
            this.real = real;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            switch (method.getName()) {
                case "executeBatch": {
                    BatchCallCounter.executeBatchCalls.incrementAndGet();
                    Object result = method.invoke(real, args);
                    BatchCallCounter.totalRowsInsertedViaBatch.addAndGet(((int[]) result).length);
                    return result;
                }
                case "executeUpdate":
                    BatchCallCounter.executeUpdateCalls.incrementAndGet();
                    return method.invoke(real, args);
                default:
                    return method.invoke(real, args);
            }
        }
    }
}
