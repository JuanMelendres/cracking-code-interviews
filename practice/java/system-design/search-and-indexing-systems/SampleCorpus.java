import java.util.List;

public class SampleCorpus {
    public static InvertedIndex build() {
        InvertedIndex index = new InvertedIndex();
        for (Document doc : documents()) {
            index.index(doc);
        }
        return index;
    }

    public static List<Document> documents() {
        return List.of(
                new Document(1, "GC Tuning Guide",
                        "java garbage collection tuning guide for production heaps"),
                new Document(2, "Keyword-Stuffed Long Page",
                        "java java java java collection collection performance database "
                                + "database database networking security testing deployment "
                                + "monitoring logging configuration scaling reliability observability"),
                new Document(3, "Python GC Internals",
                        "python garbage collection internals reference counting cycle detector"),
                new Document(4, "Database Indexing Guide",
                        "database indexing and query performance tuning for postgres"),
                new Document(5, "Java Concurrency Basics",
                        "java concurrency and thread safety fundamentals"),
                new Document(6, "Distributed Consensus",
                        "distributed systems consensus algorithms raft paxos leader election"));
    }
}
