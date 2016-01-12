package gr8.rest.producer;

import kafka.serializer.StringDecoder;
import kafka.serializer.StringEncoder;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class SpringBootKafkaProducer {

    private static final Logger logger = LoggerFactory.getLogger(SpringBootKafkaProducer.class);
    @Value("${brokerList}")
    private String brokerList;

    @Value("${sync}")
    private String sync;

    @Value("${topic}")
    private String topic;

    @Value("${kafka.enabled}")
    Boolean kafkaEnabled;

    private Producer<String, String> producer;

    public SpringBootKafkaProducer() {
    }

    @PostConstruct
    public void initIt() {
        Properties kafkaProps = new Properties();

        kafkaProps.put("bootstrap.servers", brokerList);

        kafkaProps.put("key.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");
        kafkaProps.put("value.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");
        kafkaProps.put("acks", "1");

        kafkaProps.put("retries", "1");
        kafkaProps.put("linger.ms", 5);

        producer = new KafkaProducer<>(kafkaProps);

    }

    public void send(String value) throws ExecutionException, InterruptedException {
        if(!kafkaEnabled){
            return;
        }

        if (sync.equalsIgnoreCase("sync")) {
            sendSync(value);
        } else {
            sendAsync(value);
        }
    }

    private void sendSync(String value) throws ExecutionException,
            InterruptedException {
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, value);
        producer.send(record).get();

    }

    private void sendAsync(String value) {
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, value);

        producer.send(record, (RecordMetadata recordMetadata, Exception e) -> {
            logger.info("Received callback data: " + recordMetadata.toString());
            if (e != null) {
                //This will trip on grails development mode due to spring reloader.  Not sure why
                logger.info("Received exception on callback: " + e.getMessage());
            }
        });
    }
}
