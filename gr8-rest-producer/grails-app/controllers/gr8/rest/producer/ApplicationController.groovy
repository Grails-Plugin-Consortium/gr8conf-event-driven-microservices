package gr8.rest.producer

import grails.core.GrailsApplication
import grails.plugins.GrailsPluginManager
import grails.plugins.PluginManagerAware

import static grails.async.Promises.task

class ApplicationController implements PluginManagerAware {

    GrailsApplication grailsApplication
    GrailsPluginManager pluginManager

    def index() {
        task {
            respond([grailsApplication: grailsApplication, pluginManager: pluginManager])
        }

    }
}
