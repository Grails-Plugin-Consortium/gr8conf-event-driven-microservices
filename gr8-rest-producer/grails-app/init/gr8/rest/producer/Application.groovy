package gr8.rest.producer

import gr8.rest.api.config.KafkaConfiguration
import gr8.rest.api.config.RabbitConfiguration
import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration
import org.springframework.amqp.rabbit.annotation.EnableRabbit
import org.springframework.context.annotation.Import

@Import([RabbitConfiguration, KafkaConfiguration])
@EnableRabbit
class Application extends GrailsAutoConfiguration {
    static void main(String[] args) {
        GrailsApp.run(Application, args)
    }
}