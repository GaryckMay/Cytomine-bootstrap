package be.cytomine.command.abstractimage

/**
 * Created by IntelliJ IDEA.
 * User: lrollus
 * Date: 16/02/11
 * Time: 14:57
 * To change this template use File | Settings | File Templates.
 */
import grails.converters.JSON

import be.cytomine.command.UndoRedoCommand
import be.cytomine.image.AbstractImage
import be.cytomine.command.DeleteCommand
import java.util.prefs.BackingStoreException

class DeleteAbstractImageCommand extends DeleteCommand implements UndoRedoCommand{

  def execute() {
    log.info "Execute"

    try {
      def postData = JSON.parse(postData)
      AbstractImage image = AbstractImage.findById(postData.id)
      return super.deleteAndCreateDeleteMessage(postData.id, image, [image.id, image.filename] as Object[])

    } catch (NullPointerException e) {
      log.error(e)
      return [data: [success: false, errors: e.getMessage()], status: 404]
    } catch (BackingStoreException e) {
      log.error(e)
      return [data: [success: false, errors: e.getMessage()], status: 400]
    }
  }


  def undo() {
    log.info("Undo")
    def imageData = JSON.parse(data)
    AbstractImage image = AbstractImage.createFromData(imageData)
    image.id = imageData.id;
    image.save(flush: true)
    log.error "Image errors = " + image.errors

    return super.createUndoMessage(image,[image.id, annotation.imageFileName()] as Object[]);
  }



  def redo() {
    log.info("Redo")
    def postData = JSON.parse(postData)
    AbstractImage image = AbstractImage.findById(postData.id)
    String id = postData.id
    String filename =  image.filename
    image.delete(flush: true);

    return super.createRedoMessage(id,'Image',[postData.id, filename] as Object[]);
  }
}