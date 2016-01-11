package gr8.rest.api.config;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {

	public static final String PERSON_CREATED = "person.created";
	public static final String PERSON_DELETED = "person.deleted";
	public static final String EXCHANGE = "exchange";

	@Bean
	public ConnectionFactory connectionFactory() {
		return new CachingConnectionFactory("localhost");
	}

	@Bean
	public AmqpAdmin amqpAdmin() {
		return new RabbitAdmin(connectionFactory());
	}

	@Bean
	public RabbitTemplate rabbitTemplate() {
		return new RabbitTemplate(connectionFactory());
	}

	@Bean
	Queue personCreatedQueue() {
		return new Queue(PERSON_CREATED, false);
	}

	@Bean
	Queue personDeletedQueue() {
		return new Queue(PERSON_DELETED, false);
	}

	@Bean
	TopicExchange exchange() {
		return new TopicExchange(EXCHANGE);
	}

	@Bean
	Binding bindingExchangePersonCreated(Queue personCreatedQueue, TopicExchange exchange) {
		return BindingBuilder.bind(personCreatedQueue).to(exchange).with(PERSON_CREATED);
	}
	@Bean
	Binding bindingExchangePersonDeleted(Queue personDeletedQueue, TopicExchange exchange) {
		return BindingBuilder.bind(personDeletedQueue).to(exchange).with(PERSON_DELETED);
	}
}
