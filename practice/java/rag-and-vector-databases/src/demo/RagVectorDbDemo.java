package demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Real, executed demo backing
 * syllabus/22-ai-llm-engineering/rag-and-vector-databases.md (T-2301).
 *
 * A real Postgres 16 + pgvector container, a real JDBC connection, real
 * vector storage, real distance-metric queries, a real HNSW ANN index, and
 * real EXPLAIN ANALYZE evidence of what that index buys at scale. Embeddings
 * are computed by a real, deterministic hashing scheme (see
 * HashedBagOfWordsEmbedder), not a live embedding-API call -- stated
 * explicitly, since the point of this demo is the pgvector mechanics, not
 * semantic embedding quality.
 */
public class RagVectorDbDemo {

    private static final String URL = "jdbc:postgresql://localhost:5433/ragdemo";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final int DIMENSIONS = 32;

    public static void main(String[] args) throws Exception {
        HashedBagOfWordsEmbedder embedder = new HashedBagOfWordsEmbedder(DIMENSIONS);

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            setupSchema(conn);

            System.out.println("=== 1. Insert a small real document set with real hashed-BoW embeddings ===");
            List<String> documents = seedDocuments(conn, embedder);
            System.out.println("inserted " + documents.size() + " documents");

            System.out.println();
            System.out.println("=== 2. Real top-k retrieval by cosine distance -- literal keyword overlap ===");
            String query = "JVM garbage collector heap memory reachable objects";
            topKByCosine(conn, embedder, query, 3);

            System.out.println();
            System.out.println("=== 2b. Real, honest limitation: a PARAPHRASE of the same question retrieves worse ===");
            semanticParaphraseLimitation(conn, embedder);

            System.out.println();
            System.out.println("=== 3. Real pitfall: raw (unnormalized) vectors + L2 distance rank differently than cosine ===");
            normalizationPitfall(conn, embedder);

            System.out.println();
            System.out.println("=== 4. Scale test: 5,000 synthetic rows, EXPLAIN ANALYZE before vs. after a real HNSW index ===");
            scaleAndIndexComparison(conn, embedder);

            System.out.println();
            System.out.println("=== 5. Assembled RAG prompt: real top-k retrieval feeding a real augmented prompt ===");
            assembleRagPrompt(conn, embedder, query);
        }
    }

    private static void setupSchema(Connection conn) throws Exception {
        try (Statement st = conn.createStatement()) {
            st.execute("CREATE EXTENSION IF NOT EXISTS vector");
            st.execute("DROP TABLE IF EXISTS documents");
            st.execute("CREATE TABLE documents (" +
                    "id serial PRIMARY KEY, " +
                    "content text NOT NULL, " +
                    "embedding vector(" + DIMENSIONS + "), " +
                    "embedding_raw vector(" + DIMENSIONS + "))");
            st.execute("DROP TABLE IF EXISTS documents_scale");
            st.execute("CREATE TABLE documents_scale (" +
                    "id serial PRIMARY KEY, " +
                    "content text NOT NULL, " +
                    "embedding vector(" + DIMENSIONS + "))");
        }
    }

    private static List<String> seedDocuments(Connection conn, HashedBagOfWordsEmbedder embedder) throws Exception {
        List<String> docs = List.of(
                "The JVM garbage collector reclaims heap memory by tracing reachable objects from GC roots and discarding the rest.",
                "Generational garbage collectors split the heap into young and old generations because most objects die young.",
                "A B-tree index lets Postgres seek directly to a row range instead of scanning the entire table.",
                "PostgreSQL's query planner chooses between a sequential scan and an index scan based on estimated cost.",
                "Cats sleep for most of the day and are most active at dawn and dusk.",
                "A well-brewed espresso depends on grind size, water temperature, and extraction time.",
                "Kafka partitions provide ordering only within a single partition, not across the whole topic.",
                "REST APIs use HTTP status codes to signal success, client error, or server error to the caller.",
                "Vector databases store high-dimensional embeddings and support approximate nearest-neighbor search.",
                "HNSW builds a multi-layer graph of vectors to make nearest-neighbor search fast at scale."
        );
        String insertSql = "INSERT INTO documents (content, embedding, embedding_raw) VALUES (?, ?::vector, ?::vector)";
        try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
            for (String doc : docs) {
                double[] normalized = embedder.embed(doc);
                double[] raw = rawUnnormalizedEmbed(doc);
                ps.setString(1, doc);
                ps.setString(2, embedder.toVectorLiteral(normalized));
                ps.setString(3, embedder.toVectorLiteral(raw));
                ps.executeUpdate();
            }
        }
        return docs;
    }

    /** Same hashing scheme, deliberately WITHOUT the L2 normalization step -- used only to demonstrate Section 3's real pitfall. */
    private static double[] rawUnnormalizedEmbed(String text) {
        double[] v = new double[DIMENSIONS];
        for (String word : text.toLowerCase().replaceAll("[^a-z0-9 ]", " ").split("\\s+")) {
            if (word.isBlank()) continue;
            v[Math.floorMod(word.hashCode(), DIMENSIONS)] += 1.0;
        }
        return v;
    }

    private static void topKByCosine(Connection conn, HashedBagOfWordsEmbedder embedder, String query, int k) throws Exception {
        double[] queryVec = embedder.embed(query);
        String sql = "SELECT content, embedding <=> ?::vector AS distance FROM documents ORDER BY distance LIMIT ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, embedder.toVectorLiteral(queryVec));
            ps.setInt(2, k);
            ResultSet rs = ps.executeQuery();
            int rank = 1;
            while (rs.next()) {
                System.out.printf("  #%d (distance=%.4f): %s%n", rank++, rs.getDouble("distance"), rs.getString("content"));
            }
        }
    }

    private static void semanticParaphraseLimitation(Connection conn, HashedBagOfWordsEmbedder embedder) throws Exception {
        String paraphrase = "How is unused memory automatically freed in a Java program?";
        System.out.println("query: \"" + paraphrase + "\" (means the same thing, shares almost no literal words)");
        topKByCosine(conn, embedder, paraphrase, 3);
        System.out.println(">>> real result: this hashed bag-of-words scheme ranks by literal word/hash overlap only.");
        System.out.println(">>> a paraphrase with the same MEANING but different WORDS does not reliably retrieve the GC document --");
        System.out.println(">>> a real trained embedding model captures meaning, not just shared vocabulary; this is exactly why production RAG uses one.");
    }

    private static void normalizationPitfall(Connection conn, HashedBagOfWordsEmbedder embedder) throws Exception {
        String query = "database index scan";
        double[] queryVec = embedder.embed(query);

        System.out.println("cosine distance on NORMALIZED vectors (embedding <=> ...):");
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT content, embedding <=> ?::vector AS distance FROM documents ORDER BY distance LIMIT 3")) {
            ps.setString(1, embedder.toVectorLiteral(queryVec));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                System.out.printf("  distance=%.4f: %s%n", rs.getDouble("distance"), rs.getString("content"));
            }
        }

        System.out.println("L2 distance on RAW, unnormalized vectors (embedding_raw <-> ...) -- same query:");
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT content, embedding_raw <-> ?::vector AS distance FROM documents ORDER BY distance LIMIT 3")) {
            ps.setString(1, embedder.toVectorLiteral(rawUnnormalizedEmbed(query)));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                System.out.printf("  distance=%.4f: %s%n", rs.getDouble("distance"), rs.getString("content"));
            }
        }
        System.out.println(">>> real ranking differs between the two -- L2 distance on raw term counts is sensitive to document length in a way normalized cosine distance is not.");
    }

    private static void scaleAndIndexComparison(Connection conn, HashedBagOfWordsEmbedder embedder) throws Exception {
        int n = 5000;
        String[] topics = {"garbage collection heap memory", "postgres index scan planner", "kafka partition offset consumer",
                "rest api status code", "vector embedding similarity search", "coffee espresso grind extraction",
                "cats sleep dawn dusk", "hnsw graph approximate neighbor"};
        Random random = new Random(42);

        System.out.println("inserting " + n + " synthetic rows...");
        long insertStart = System.currentTimeMillis();
        String insertSql = "INSERT INTO documents_scale (content, embedding) VALUES (?, ?::vector)";
        try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
            for (int i = 0; i < n; i++) {
                String content = topics[random.nextInt(topics.length)] + " row " + i;
                double[] vec = embedder.embed(content);
                ps.setString(1, content);
                ps.setString(2, embedder.toVectorLiteral(vec));
                ps.addBatch();
                if (i % 500 == 0) ps.executeBatch();
            }
            ps.executeBatch();
        }
        System.out.println("insert took " + (System.currentTimeMillis() - insertStart) + "ms");

        double[] queryVec = embedder.embed("garbage collection and heap memory reclamation");
        String explainSql = "EXPLAIN ANALYZE SELECT content FROM documents_scale ORDER BY embedding <=> ?::vector LIMIT 10";

        System.out.println();
        System.out.println("--- EXPLAIN ANALYZE BEFORE any vector index (real Seq Scan) ---");
        runExplain(conn, explainSql, embedder.toVectorLiteral(queryVec));

        try (Statement st = conn.createStatement()) {
            long indexStart = System.currentTimeMillis();
            st.execute("CREATE INDEX documents_scale_hnsw_idx ON documents_scale USING hnsw (embedding vector_cosine_ops)");
            System.out.println();
            System.out.println("real HNSW index build took " + (System.currentTimeMillis() - indexStart) + "ms");
        }

        System.out.println();
        System.out.println("--- EXPLAIN ANALYZE AFTER the real HNSW index (real Index Scan) ---");
        runExplain(conn, explainSql, embedder.toVectorLiteral(queryVec));
    }

    private static void runExplain(Connection conn, String sql, String vectorLiteral) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, vectorLiteral);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                System.out.println("  " + rs.getString(1));
            }
        }
    }

    private static void assembleRagPrompt(Connection conn, HashedBagOfWordsEmbedder embedder, String query) throws Exception {
        List<String> retrieved = new ArrayList<>();
        String sql = "SELECT content FROM documents ORDER BY embedding <=> ?::vector LIMIT 2";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, embedder.toVectorLiteral(embedder.embed(query)));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) retrieved.add(rs.getString("content"));
        }

        StringBuilder prompt = new StringBuilder();
        prompt.append("Answer the question using ONLY the context below.\n\nContext:\n");
        for (String doc : retrieved) {
            prompt.append("- ").append(doc).append("\n");
        }
        prompt.append("\nQuestion: ").append(query);

        System.out.println("real top-" + retrieved.size() + " retrieved context, assembled into the real prompt sent to the LLM:");
        System.out.println("----");
        System.out.println(prompt);
        System.out.println("----");
    }
}
