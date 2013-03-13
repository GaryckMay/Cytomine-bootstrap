package be.cytomine.image.server

import be.cytomine.SecurityACL
import be.cytomine.processing.ProcessingServer
import grails.plugins.springsecurity.Secured

class ProcessingServerService {

    def cytomineService

    def list() {
        SecurityACL.checkUser(cytomineService.currentUser)
        ProcessingServer.list()
    }

    def read(long id) {
        SecurityACL.checkUser(cytomineService.currentUser)
        return ProcessingServer.read(id)
    }

}
