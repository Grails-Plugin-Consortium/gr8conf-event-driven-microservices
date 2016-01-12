package gr8.rest.producer

import gr8.rest.api.config.RabbitConfiguration
import grails.converters.JSON
import grails.rest.RestfulController
import grails.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired

import static org.springframework.http.HttpStatus.*

@Transactional(readOnly = false)
class PersonController extends RestfulController {

    @Autowired
    EventEmitterService eventEmitterService

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

        eventEmitterService.emitEvent((person as JSON).toString())

        respond person, [status: CREATED, view: "show"]
    }
}
