import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * Shared setup so each demo is runnable standalone, in any order, against a
 * fresh broker -- without this, a demo run before ProducerPartitionKeyDemo
 * would hit Kafka's auto-create-topic default (1 partition), silently
 * contradicting every "4 partitions" claim in the Week 8 chapters.
 */
final class KafkaSupport {
    static void ensureTopic(String bootstrap, String topic, int partitions) throws Exception {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
        try (AdminClient admin = AdminClient.create(props)) {
            admin.createTopics(List.of(new NewTopic(topic, partitions, (short) 1))).all().get();
        } catch (ExecutionException e) {
            if (!e.getMessage().contains("already exists")) throw e;
        }
    }
}
