package gr8.rest.api.config;

import gr8.rest.producer.SpringBootKafkaProducer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfiguration {

    @Bean
    public SpringBootKafkaProducer springBootKafkaProducer() {
        return new SpringBootKafkaProducer();
    }
}
