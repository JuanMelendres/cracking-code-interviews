import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * T-702 / T-705 -- producer fundamentals + partition-key routing.
 *
 * Sends the same key repeatedly and different keys, then prints the
 * partition each record actually landed on. Demonstrates: (1) same key
 * always maps to the same partition (ordering guarantee is per-key, not
 * global), (2) null key round-robins (in the sticky partitioner, batches
 * per-partition rather than strict per-record round robin).
 */
public class ProducerPartitionKeyDemo {
    public static void main(String[] args) throws Exception {
        String topic = "orders";
        String bootstrap = "localhost:9092";

        try (AdminClient admin = AdminClient.create(adminProps(bootstrap))) {
            admin.createTopics(List.of(new NewTopic(topic, 4, (short) 1))).all().get();
        } catch (ExecutionException e) {
            if (!e.getMessage().contains("already exists")) throw e;
        }

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(props)) {
            System.out.println("== same key -> same partition, every time ==");
            for (int i = 0; i < 6; i++) {
                RecordMetadata md = producer.send(
                        new ProducerRecord<>(topic, "customer-42", "order-" + i)).get();
                System.out.printf("key=customer-42 value=order-%d -> partition=%d offset=%d%n",
                        i, md.partition(), md.offset());
            }

            System.out.println("== different keys -> spread across partitions ==");
            String[] keys = {"customer-1", "customer-2", "customer-3", "customer-4", "customer-5", "customer-6"};
            for (String key : keys) {
                RecordMetadata md = producer.send(
                        new ProducerRecord<>(topic, key, "order-for-" + key)).get();
                System.out.printf("key=%-12s -> partition=%d offset=%d%n", key, md.partition(), md.offset());
            }

            System.out.println("== null key -> sticky partitioner batches onto one partition per batch ==");
            for (int i = 0; i < 6; i++) {
                RecordMetadata md = producer.send(
                        new ProducerRecord<>(topic, null, "unkeyed-" + i)).get();
                System.out.printf("key=null value=unkeyed-%d -> partition=%d offset=%d%n", i, md.partition(), md.offset());
            }
        }
    }

    private static Properties adminProps(String bootstrap) {
        Properties p = new Properties();
        p.put(org.apache.kafka.clients.admin.AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
        return p;
    }
}
