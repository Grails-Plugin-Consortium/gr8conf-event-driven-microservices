package gr8.rest.producer

import gr8.rest.api.config.KafkaConfiguration
import gr8.rest.api.config.RabbitConfiguration
import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration
import groovy.util.logging.Slf4j
import org.springframework.amqp.rabbit.annotation.EnableRabbit
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.context.annotation.Import
import org.springframework.scheduling.annotation.EnableAsync

@Import([RabbitConfiguration, KafkaConfiguration])
@EnableRabbit
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableAsync
@Slf4j
class Application extends GrailsAutoConfiguration {
    static void main(String[] args) {
        GrailsApp.run(Application, args)
    }

//    @Bean
//    @ConditionalOnProperty(value = "sample.zipkin.enabled", havingValue = "false")
//    public ZipkinSpanReporter spanCollector() {
//        return new ZipkinSpanReporter() {
//            @Override
//            public void report(Span span) {
//                log.info(String.format("Producer reporting span [%s]", span));
//            }
//        };
//    }
}