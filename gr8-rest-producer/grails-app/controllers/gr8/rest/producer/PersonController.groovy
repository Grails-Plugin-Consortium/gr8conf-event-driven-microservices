package gr8.rest.producer

import gr8.rest.api.config.RabbitConfiguration
import grails.converters.JSON
import grails.rest.RestfulController
import grails.transaction.Transactional
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired

import static org.springframework.http.HttpStatus.*

@Transactional(readOnly = false)
class PersonController extends RestfulController {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    SpringBootKafkaProducer springBootKafkaProducer;


    static responseFormats = ['json', 'xml']
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    PersonController() {
        super(Person)
    }

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Person.list(params), model: [personCount: Person.count()]
    }

    def show(Person person) {
        respond person
    }

    @Transactional
    def save(Person person) {
        if (person == null) {
            transactionStatus.setRollbackOnly()
            render status: NOT_FOUND
            return
        }

        if (person.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond person.errors, view: 'create'
            return
        }

        person.save(flush: true)

        emitEvent((person as JSON).toString())

        respond person, [status: CREATED, view: "show"]
    }

    void emitEvent(String messageBody) {
        //emit a kafka event to the producer
        try {
            springBootKafkaProducer.send(messageBody);
        } catch (Exception e) {
            log.error('Could not emit event to kafka', e)
        }

        //emit a rabbitmq event to a queue
        try {
            rabbitTemplate.convertAndSend(RabbitConfiguration.EXCHANGE, RabbitConfiguration.PERSON_CREATED, messageBody);
        } catch (Exception e) {
            log.error('Could not emit event to RabbitMQ', e)
        }
    }

    @Transactional
    def update(Person person) {
        if (person == null) {
            transactionStatus.setRollbackOnly()
            render status: NOT_FOUND
            return
        }

        if (person.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond person.errors, view: 'edit'
            return
        }

        person.save flush: true

        respond person, [status: OK, view: "show"]
    }

    @Transactional
    def delete(Person person) {

        if (person == null) {
            transactionStatus.setRollbackOnly()
            render status: NOT_FOUND
            return
        }

        person.delete flush: true

        emitEvent(RabbitConfiguration.EXCHANGE, RabbitConfiguration.PERSON_DELETED, (person as JSON).toString())

        render status: NO_CONTENT
    }
}
