package gr8.rest.producer

class Person {
    String name

    static constraints = {
        name blank: false
    }
}