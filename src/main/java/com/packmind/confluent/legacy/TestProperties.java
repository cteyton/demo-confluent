import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class TestProperties {

        public void badInitProperties() {
                // packmind-ignore
                final Properties props = new Properties();
                props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "SERVER");
                props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                                StringSerializer.class);
                props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                                String.class);

                KafkaProducer<String, String> producer = new KafkaProducer<>(props);
        }

        public void goodInitProperties() {
                final Properties props = new Properties();
                props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "SERVER");
                props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                                StringSerializer.class);
                props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                                String.class);
                // Here we override custom properties
                props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "gzip");

                KafkaProducer<String, String> producer = new KafkaProducer<>(props);
        }

}