package be.cytomine.command.annotationterm

import be.cytomine.Exception.ObjectNotFoundException
import be.cytomine.command.DeleteCommand
import be.cytomine.command.UndoRedoCommand
import be.cytomine.ontology.Annotation
import be.cytomine.ontology.AnnotationTerm
import be.cytomine.ontology.Term
import be.cytomine.security.User
import grails.converters.JSON

class DeleteAnnotationTermCommand extends DeleteCommand implements UndoRedoCommand {

    boolean saveOnUndoRedoStack = true;

    def execute() {
        Annotation annotation = Annotation.get(json.annotation)
        Term term = Term.get(json.term)
        User user = User.get(json.user)
        log.info "Delete annotation-term with annotation=" + annotation + " term=" + term + " user=" + user

        AnnotationTerm annotationTerm = AnnotationTerm.findWhere('annotation': annotation, 'term': term, 'user': user)
        if (!annotationTerm) throw new ObjectNotFoundException("Annotation term not found ($annotation,$term,$user)")
        String id = annotationTerm.id
        super.initCurrentCommantProject(annotationTerm.annotation.image.project)
        def response = super.createDeleteMessage(id, annotationTerm, [id, annotation.id, term.name, user?.username] as Object[])
        AnnotationTerm.unlink(annotationTerm.annotation, annotationTerm.term, annotationTerm.user)

        return response
    }

    def undo() {
        log.info("Undo")
        def annotationTermData = JSON.parse(data)
        def annotation = Annotation.get(annotationTermData.annotation)
        def term = Term.get(annotationTermData.term)
        def user = User.get(annotationTermData.user)

        AnnotationTerm annotationTerm = AnnotationTerm.createFromData(annotationTermData)
        annotationTerm = AnnotationTerm.link(annotationTermData.id, annotation, term, user)

        HashMap<String, Object> callback = new HashMap<String, Object>();
        callback.put("annotationID", annotation.id)
        callback.put("termID", term.id)
        callback.put("imageID", annotation.image.id)

        return super.createUndoMessage(annotationTerm, [id, annotation.id, term.name, user?.username] as Object[], callback
        );
    }

    def redo() {
        log.info("Redo")
        def postData = JSON.parse(postData)
        Annotation annotation = Annotation.get(postData.annotation)
        Term term = Term.get(postData.term)
        User user = User.get(postData.user)

        AnnotationTerm annotationTerm = AnnotationTerm.findWhere('annotation': annotation, 'term': term, 'user': user)
        String id = annotationTerm.id
        AnnotationTerm.unlink(annotationTerm.annotation, annotationTerm.term, annotationTerm.user)

        HashMap<String, Object> callback = new HashMap<String, Object>();
        callback.put("annotationID", annotation.id)
        callback.put("termID", term.id)
        callback.put("imageID", annotation.image.id)

        return super.createRedoMessage(id, annotationTerm, [id, annotation.id, term.name, user?.username] as Object[], callback
        );
    }

}