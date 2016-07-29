package gr8.rest.stream.api;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface PersonChannel {

//    @Output("personRabbit")
//    MessageChannel rabbitSave();

    @Output("personKafka")
    MessageChannel kafkaSave();

}
