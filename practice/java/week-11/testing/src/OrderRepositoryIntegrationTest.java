import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * T-1104 -- an integration test against a REAL Postgres, not a mock, not
 * an in-memory fake. This repository's whole job is translating Java
 * calls into real SQL against a real database; a mock would test nothing
 * but the test's own assumptions about what Postgres does. The real
 * dependency is provisioned by Docker directly (see this pack's README)
 * rather than via the Testcontainers library itself -- Testcontainers'
 * own dependency tree (docker-java, its transports, jna, etc.) doesn't
 * fit this repository's plain-jar, no-build-tool convention, but the
 * TECHNIQUE demonstrated here is identical to what Testcontainers
 * automates: a real, ephemeral, Docker-provisioned dependency for the
 * test, not a mock standing in for one.
 */
public class OrderRepositoryIntegrationTest {

    static final String JDBC_URL = "jdbc:postgresql://localhost:5434/week11";

    @Test
    void insertedOrderIsReallyPersistedAndReadableBack() throws Exception {
        OrderRepository repo = new OrderRepository(JDBC_URL);
        long id = repo.insert("integration-test-customer", 7777);
        long amount = repo.findAmountById(id);
        assertEquals(7777, amount, "the amount read back must be the exact real value Postgres stored");
    }
}
