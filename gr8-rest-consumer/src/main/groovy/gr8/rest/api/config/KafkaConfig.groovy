package gr8.rest.api.config

import gr8.rest.consumer.Person
import grails.converters.JSON
import kafka.consumer.Consumer
import kafka.consumer.ConsumerConfig
import kafka.consumer.ConsumerIterator
import kafka.consumer.KafkaStream
import kafka.javaapi.consumer.ConsumerConnector
import kafka.serializer.StringDecoder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Service

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@Configuration
public class KafkaConfig {

    @Value('${spring.cloud.zookeeper.connect-string}')
    String zookeeperHostPort


    @Bean(destroyMethod = "shutdown")
    public ConsumerConnector consumerConnector() {

        ConsumerConfig config = consumerConfig();

        return Consumer.createJavaConsumerConnector(config);
    }

    @Bean
    public ExecutorService kafkaConsumerThreadPool() {
        return Executors.newFixedThreadPool(4);
    }

    public ConsumerConfig consumerConfig() {

        Properties props = new Properties();
        props.put("zookeeper.connect", zookeeperHostPort);
        props.put("group.id", "gr8-rest-client");
        props.put("zookeeper.session.timeout.ms", "400");
        props.put("zookeeper.sync.time.ms", "200");
        props.put("auto.commit.interval.ms", "1000");
        props.put("auto.offset.reset", "smallest");
        return new ConsumerConfig(props);
    }


    @Service
    public static class KafkaConsumerService {

        private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

        public static final String TOPIC = "person";
        public static final int PARTITIONS = 1;
        @Autowired
        @Qualifier("kafkaConsumerThreadPool")
        ExecutorService executor;

        @Autowired
        ConsumerConnector consumerConnector;

        @PostConstruct
        public void run() {
            Map<String, Integer> topicCountMap = new HashMap<>();
            topicCountMap.put(TOPIC, PARTITIONS);


            Map<String, List<KafkaStream<String, String>>> consumerMap =
                    consumerConnector.createMessageStreams(topicCountMap, new StringDecoder(null), new StringDecoder(null));
            List<KafkaStream<String, String>> streams = consumerMap.get(TOPIC);

            int threadNumber = 0;
            logger.info("{} streams found for topic {}", streams.size(), TOPIC);
            for (final KafkaStream<String, String> stream : streams) {
                executor.submit(new ExampleKafkaEventConsumer(stream, threadNumber));
                threadNumber++;

                if (threadNumber > PARTITIONS)
                    throw new IllegalStateException("The kafka topic " + TOPIC + " has more partitions than configured consumer threads.");
            }
        }

        @PreDestroy
        public void shutdown() {
            executor.shutdown();

            try {
                if (!executor.awaitTermination(5000, TimeUnit.MILLISECONDS)) {
                    logger.warn("Timed out waiting for consumer threads to shut down, exiting uncleanly");
                }
            } catch (InterruptedException e) {
                logger.warn("Interrupted during shutdown, exiting uncleanly");
            }
        }
    }

    public static class ExampleKafkaEventConsumer implements Runnable {

        private static final Logger logger = LoggerFactory.getLogger(ExampleKafkaEventConsumer.class);

        private final KafkaStream<String, String> stream;
        private final int threadNumber;

        public ExampleKafkaEventConsumer(KafkaStream<String, String> stream, int threadNumber) {

            this.stream = stream;
            this.threadNumber = threadNumber;
        }

        @Override
        public void run() {
            ConsumerIterator<String, String> it = stream.iterator();

            logger.info("Running the consumer of thread #{}", threadNumber);

            while (it.hasNext()) {
                logger.info("Thread " + threadNumber + ": " + new Person(JSON.parse(it.next().message())));
            }

            logger.info("Shutting down Thread: " + threadNumber);
        }
    }
}