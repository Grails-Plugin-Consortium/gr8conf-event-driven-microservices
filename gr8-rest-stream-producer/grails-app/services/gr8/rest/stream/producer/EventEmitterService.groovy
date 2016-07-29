package gr8.rest.stream.producer

import gr8.rest.stream.api.PersonChannel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.support.MessageBuilder

class EventEmitterService {

    @Autowired
    private PersonChannel personChannel;

    public void emitEvents(String messageBody) {
        log.info("You are going to emit an event to Kafka!")
        try {
            personChannel.kafkaSave().send(MessageBuilder.withPayload(messageBody).build())
        } catch (Exception e) {
            log.error('Could not emit event to Kafka', e)
        }
    }
}
