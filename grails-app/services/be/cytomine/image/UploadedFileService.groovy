package be.cytomine.image


import be.cytomine.command.*
import be.cytomine.security.SecUser
import be.cytomine.security.User
import be.cytomine.utils.ModelService
import be.cytomine.utils.Task

class UploadedFileService extends ModelService {

    static transactional = true
    def cytomineService
    def securityACLService


    def dataSource

    def currentDomain() {
        return UploadedFile
    }

    def list(User user) {
        securityACLService.checkIsSameUser(user, cytomineService.currentUser)
        def uploadedFiles = UploadedFile.createCriteria().list(sort : "created", order : "desc") {
            eq("user.id", user.id)
            isNull("parent.id")
            isNull("downloadParent.id")
        }
        return uploadedFiles
    }

    UploadedFile read(def id) {
        UploadedFile.read(id)
    }

    UploadedFile get(def id) {
        UploadedFile.get(id)
    }


    /**
     * Add the new domain with JSON data
     * @param json New domain data
     * @return Response structure (created domain data,..)
     */
    def add(def json) {
        SecUser currentUser = cytomineService.getCurrentUser()
        return executeCommand(new AddCommand(user: currentUser),null,json)
    }

    /**
     * Update this domain with new data from json
     * @param domain Domain to update
     * @param jsonNewData New domain datas
     * @return  Response structure (new domain data, old domain data..)
     */
    def update(UploadedFile uploadedFile, def jsonNewData) {
        SecUser currentUser = cytomineService.getCurrentUser()
        return executeCommand(new EditCommand(user: currentUser), uploadedFile,jsonNewData)
    }

    /**
     * Delete this domain
     * @param domain Domain to delete
     * @param transaction Transaction link with this command
     * @param task Task for this command
     * @param printMessage Flag if client will print or not confirm message
     * @return Response structure (code, old domain,..)
     */
    def delete(UploadedFile domain, Transaction transaction = null, Task task = null, boolean printMessage = true) {
        SecUser currentUser = cytomineService.getCurrentUser()
        Command c = new DeleteCommand(user: currentUser,transaction:transaction)
        return executeCommand(c,domain,null)
    }

    def getStringParamsI18n(def domain) {
        return [domain.id, domain.filename]
    }


    def deleteDependentUploadedFile(UploadedFile uploadedFile, Transaction transaction,Task task=null) {

        taskService.updateTask(task,task? "Delete ${UploadedFile.countByParent(uploadedFile)} uploadedFile parents":"")

        UploadedFile.findAllByParent(uploadedFile).each {
            this.delete(it,transaction,null, false)
        }
    }
}