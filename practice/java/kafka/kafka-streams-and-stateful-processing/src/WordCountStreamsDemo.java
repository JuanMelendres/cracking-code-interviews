import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.Stores;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class WordCountStreamsDemo {
    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "word-count-demo-app");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 200);

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> lines = builder.stream("streams-input");
        Materialized<String, Long, KeyValueStore<Bytes, byte[]>> materialized =
                Materialized.<String, Long>as(Stores.inMemoryKeyValueStore("word-counts-store"))
                        .withKeySerde(Serdes.String())
                        .withValueSerde(Serdes.Long());
        KTable<String, Long> counts = lines
                .flatMapValues(line -> Arrays.asList(line.toLowerCase().split("\\s+")))
                .groupBy((key, word) -> word)
                .count(materialized);
        counts.toStream().to("streams-output", Produced.with(Serdes.String(), Serdes.Long()));

        Topology topology = builder.build();
        System.out.println(topology.describe());

        KafkaStreams streams = new KafkaStreams(topology, props);
        CountDownLatch latch = new CountDownLatch(1);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            streams.close();
            latch.countDown();
        }));
        streams.start();
        latch.await();
    }
}
