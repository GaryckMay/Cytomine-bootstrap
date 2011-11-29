package be.cytomine.image

import be.cytomine.Exception.CytomineException
import be.cytomine.Exception.ObjectNotFoundException
import be.cytomine.ModelService
import be.cytomine.command.AddCommand
import be.cytomine.command.DeleteCommand
import be.cytomine.security.Group
import be.cytomine.security.User
import org.codehaus.groovy.grails.web.json.JSONObject

class AbstractImageGroupService extends ModelService {

    static transactional = true
    def cytomineService
    def commandService
    def responseService
    def domainService

    boolean saveOnUndoRedoStack = true

    def get(AbstractImage abstractimage, Group group) {
        AbstractImageGroup.findByAbstractimageAndGroup(abstractimage, group)
    }

    def add(def json) throws CytomineException {
        User currentUser = cytomineService.getCurrentUser()
        return executeCommand(new AddCommand(user: currentUser), json)
    }

    def delete(def json) throws CytomineException {
        User currentUser = cytomineService.getCurrentUser()
        return executeCommand(new DeleteCommand(user: currentUser), json)
    }

    def update(def json) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Restore domain which was previously deleted
     * @param json domain info

     * @param printMessage print message or not
     * @return response
     */
    def restore(JSONObject json, boolean printMessage) {
        restore(AbstractImageGroup.createFromDataWithId(json),printMessage)
    }
    def restore(AbstractImageGroup domain, boolean printMessage) {
        //Save new object
        domain = AbstractImageGroup.link(domain.abstractimage, domain.group)
        //Build response message
        return responseService.createResponseMessage(domain,[domain.id, domain.abstractimage.filename, domain.group.name],printMessage,"Add",domain.getCallBack())
    }
    /**
     * Destroy domain which was previously added
     * @param json domain info

     * @param printMessage print message or not
     * @return response
     */
    def destroy(JSONObject json, boolean printMessage) {
        //Get object to delete
        log.debug "json=" + json

         destroy(retrieve(json),printMessage)
    }
    def destroy(AbstractImageGroup domain, boolean printMessage) {
        //Build response message
        def response = responseService.createResponseMessage(domain,[domain.id, domain.abstractimage.filename, domain.group.name],printMessage,"Delete",domain.getCallBack())
        //Delete object
        AbstractImageGroup.unlink(domain.abstractimage, domain.group)
        return response
    }

    /**
     * Create domain from JSON object
     * @param json JSON with new domain info
     * @return new domain
     */
    AbstractImageGroup createFromJSON(def json) {
       return AbstractImageGroup.createFromData(json)
    }

    /**
     * Retrieve domain thanks to a JSON object
     * @param json JSON with new domain info
     * @return domain retrieve thanks to json
     */
    def retrieve(JSONObject json) {
        AbstractImage abstractimage = AbstractImage.get(json.abstractimage)
        Group group = Group.get(json.group)
        AbstractImageGroup domain = AbstractImageGroup.findByAbstractimageAndGroup(abstractimage, group)
        if(!domain) throw new ObjectNotFoundException("AbstractImageGroup group=${json.group} image=${json.abstractimage} not found")
        return domain
    }
}