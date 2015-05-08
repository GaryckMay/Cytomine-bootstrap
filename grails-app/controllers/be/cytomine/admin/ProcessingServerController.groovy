package be.cytomine.admin

import be.cytomine.processing.ProcessingServer
import grails.plugin.springsecurity.annotation.Secured

@Secured(['ROLE_ADMIN','ROLE_SUPER_ADMIN'])
class ProcessingServerController {

    def scaffold = ProcessingServer
}