package gr8.rest.producer

import gr8.rest.api.config.RabbitConfiguration
import grails.converters.JSON
import grails.events.Events
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value

class EventEmitterService implements Events {

    static transactional = false

    @Value('${kafka.enabled}')
    Boolean kafkaEnabled

    @Value('${rabbitmq.enabled}')
    Boolean rabbitmqEnabled

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    SpringBootKafkaProducer springBootKafkaProducer;

    public void emitEvent(String messageBody) {
        //emit a kafka event to the producer
        log.info("You are going to emit an event to Kafka!")
        try {
            if (kafkaEnabled) {
                springBootKafkaProducer.send(messageBody);
            } else {
                log.info('Kafka event not emitted, because disabled.')
            }
        } catch (Exception e) {
            log.error('Could not emit event to Kafka', e)
        }

        //emit a rabbitmq event to a queue
        try {
            if (rabbitmqEnabled) {
                rabbitTemplate.convertAndSend(RabbitConfiguration.EXCHANGE, RabbitConfiguration.PERSON_CREATED, messageBody);
            } else {
                log.info('RabbitMQ event not emitted, because disabled.')
            }
        } catch (Exception e) {
            log.error('Could not emit event to RabbitMQ', e)
        }

    }
}
