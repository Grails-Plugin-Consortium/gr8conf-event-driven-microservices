package gr8.rest.stream.producer

class Person {
    String name

    static constraints = {
        name blank: false
    }
}