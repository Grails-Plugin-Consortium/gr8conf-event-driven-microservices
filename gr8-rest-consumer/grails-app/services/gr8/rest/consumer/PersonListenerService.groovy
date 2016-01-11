package gr8.rest.consumer

import grails.converters.JSON
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import reactor.spring.context.annotation.Consumer

import javax.annotation.PostConstruct


class PersonListenerService {

	@Autowired
	RabbitTemplate rabbitTemplate

	@PostConstruct
	void postConstruct() {
		println 'service exists'
	}

	@RabbitListener(queues = "queue.person")
	public void receiveMessage(String person) {
		println 'PersonListenerService::' + new Person(JSON.parse(person))
	}
}
