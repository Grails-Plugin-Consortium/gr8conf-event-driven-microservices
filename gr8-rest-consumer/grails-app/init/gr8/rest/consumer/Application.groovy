package gr8.rest.consumer

import gr8.rest.api.config.KafkaConfig
import gr8.rest.api.config.RabbitConfiguration
import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration
import grails.converters.JSON
import groovy.util.logging.Slf4j
import org.springframework.amqp.rabbit.annotation.EnableRabbit
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.context.annotation.Import
import org.springframework.cloud.sleuth.zipkin.ZipkinSpanReporter
import org.springframework.scheduling.annotation.EnableAsync
import zipkin.Span

import javax.annotation.PostConstruct

@Import([RabbitConfiguration, KafkaConfig])
@EnableRabbit
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableAsync
@Slf4j
class Application extends GrailsAutoConfiguration {
	static void main(String[] args) {
		GrailsApp.run(Application, args)
	}

	@Bean
	Receiver receiver() {
		new Receiver()
	}

	@Bean
	@ConditionalOnProperty(value = "sample.zipkin.enabled", havingValue = "false")
	public ZipkinSpanReporter spanCollector() {
		return new ZipkinSpanReporter() {
			@Override
			public void report(Span span) {
				log.info(String.format("Consumer reporting span [%s]", span));
			}
		};
	}
}

class Receiver {

	@PostConstruct
	void postConstruct() {
		println 'receiver exists'
	}

	@RabbitListener(queues = RabbitConfiguration.PERSON_CREATED)
	public void personCreated(String person) {
		println 'Receiver::person::created::' + new Person(JSON.parse(person))
	}

	@RabbitListener(queues = RabbitConfiguration.PERSON_DELETED)
	public void personDeleted(String person) {
		println 'Receiver::person::deleted::' + new Person(JSON.parse(person))
	}
}
