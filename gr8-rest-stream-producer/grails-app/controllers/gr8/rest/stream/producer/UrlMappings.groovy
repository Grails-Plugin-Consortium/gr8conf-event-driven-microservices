package gr8.rest.stream.producer

class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/person"(resources:"person")

        "/"(controller: 'application', action:'index')
        "500"(view: '/application/serverError')
        "404"(view: '/application/notFound')
    }
}
