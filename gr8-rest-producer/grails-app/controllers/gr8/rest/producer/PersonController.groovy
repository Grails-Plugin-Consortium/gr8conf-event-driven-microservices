package gr8.rest.producer

import grails.converters.JSON
import grails.rest.RestfulController
import grails.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import reactor.spring.context.annotation.Consumer
import reactor.spring.context.annotation.Selector

import static org.springframework.http.HttpStatus.CREATED
import static org.springframework.http.HttpStatus.NOT_FOUND

@Transactional(readOnly = false)
@Consumer
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

        //fire some events
        //notify "person:saved", (person as JSON).toString()
        eventEmitterService.emitEvent((person as JSON).toString())

        respond person, [status: CREATED, view: "show"]
    }

    @Selector("person:saved")
    public consumePersonSavedEvent(String json) {
        log.error "Internal person saved event with data $json"
    }
}
