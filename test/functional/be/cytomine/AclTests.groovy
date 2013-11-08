package be.cytomine

import be.cytomine.project.Project
import be.cytomine.security.User
import be.cytomine.test.BasicInstanceBuilder
import be.cytomine.test.HttpClient
import be.cytomine.test.Infos
import be.cytomine.test.http.AclAPI
import be.cytomine.test.http.ProjectAPI
import be.cytomine.test.http.UserAPI
import be.cytomine.utils.UpdateData
import grails.converters.JSON
import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject
import static org.springframework.security.acls.domain.BasePermission.*
/**
 * Created by IntelliJ IDEA.
 * User: lrollus
 * Date: 2/03/11
 * Time: 11:08
 * To change this template use File | Settings | File Templates.
 */
class AclTests {



    void testGetACL() {
        User user = BasicInstanceBuilder.getUserNotExist(true)
        Project project = BasicInstanceBuilder.getProjectNotExist(true)

        def result = AclAPI.list(project.class.name, project.id, user.id,Infos.GOODLOGIN, Infos.GOODPASSWORD)
        assert 200 == result.code
        def json = JSON.parse(result.data)
        assert json.collection instanceof JSONArray
        assert json.collection.isEmpty()

        assert 200 == AclAPI.create(project.class.name, project.id, user.id,"READ",Infos.GOODLOGIN, Infos.GOODPASSWORD).code
        assert 200 == AclAPI.create(project.class.name, project.id, user.id,"ADMINISTRATION",Infos.GOODLOGIN, Infos.GOODPASSWORD).code


        result = AclAPI.list(project.class.name, project.id, user.id,Infos.GOODLOGIN, Infos.GOODPASSWORD)
        assert 200 == result.code
        json = JSON.parse(result.data)
        assert json.collection instanceof JSONArray
        assert json.collection.size()==2
    }

    void testGetACLBadRequestWrongRequest() {
        def result = AclAPI.list("badname", null, null,Infos.GOODLOGIN, Infos.GOODPASSWORD)
        assert 404 == result.code
    }

    void testGetACLBadRequestObjectNotFound() {
        User user = BasicInstanceBuilder.getUserNotExist(true)
        Project project = BasicInstanceBuilder.getProjectNotExist(true)
        def result = AclAPI.list(project.class.name, 1, user.id,Infos.GOODLOGIN, Infos.GOODPASSWORD)
        assert 404 == result.code
    }

    void tesTAddACL() {
        User user = BasicInstanceBuilder.getUserNotExist(true)
        Project project = BasicInstanceBuilder.getProjectNotExist(true)

        assert (403 == ProjectAPI.show(project.id,user.username,"password").code)
        assert (403 == ProjectAPI.update(project.id,project.encodeAsJSON(),user.username,"password").code)

        def result = AclAPI.create(project.class.name, project.id, user.id,"READ",Infos.GOODLOGIN, Infos.GOODPASSWORD)
        assert 200 == result.code
        def json = JSON.parse(result.data)

        assert (200 == ProjectAPI.show(project.id,user.username,"password").code)
        assert (403 == ProjectAPI.update(project.id,project.encodeAsJSON(),user.username,"password").code)

        result = AclAPI.create(project.class.name, project.id, user.id,"ADMINISTRATION",Infos.GOODLOGIN, Infos.GOODPASSWORD)
        assert 200 == result.code

        assert (200 == ProjectAPI.show(project.id,user.username,"password").code)
        assert (200 == ProjectAPI.update(project.id,project.encodeAsJSON(),user.username,"password").code)

        assert 400 == AclAPI.create(project.class.name, project.id, user.id,"DELETE",Infos.GOODLOGIN, Infos.GOODPASSWORD).code
        assert 400 == AclAPI.create(project.class.name, project.id, user.id,"WRITE",Infos.GOODLOGIN, Infos.GOODPASSWORD).code

    }

    void tesTAddACLObjectNotFound() {
        User user = BasicInstanceBuilder.getUserNotExist(true)
        Project project = BasicInstanceBuilder.getProjectNotExist(true)

        def result = AclAPI.create(project.class.name, 1, user.id,"READ",Infos.GOODLOGIN, Infos.GOODPASSWORD)
        assert 404 == result.code
    }


    void tesTAddACLDefault() {
        User user = BasicInstanceBuilder.getUserNotExist(true)
        Project project = BasicInstanceBuilder.getProjectNotExist(true)

        assert (403 == ProjectAPI.show(project.id,user.username,"password").code)
        assert (403 == ProjectAPI.update(project.id,project.encodeAsJSON(),user.username,"password").code)

        //give null
        def result = AclAPI.create(project.class.name, project.id, user.id,null,Infos.GOODLOGIN, Infos.GOODPASSWORD)
        assert 200 == result.code
        def json = JSON.parse(result.data)

        //check if null is READ
        assert (200 == ProjectAPI.show(project.id,user.username,"password").code)
        assert (403 == ProjectAPI.update(project.id,project.encodeAsJSON(),user.username,"password").code)

    }


    void testDeleteACL() {
        User user = BasicInstanceBuilder.getUserNotExist(true)
        Project project = BasicInstanceBuilder.getProjectNotExist(true)

        assert 200 == AclAPI.create(project.class.name, project.id, user.id,"READ",Infos.GOODLOGIN, Infos.GOODPASSWORD).code
        assert 200 == AclAPI.create(project.class.name, project.id, user.id,"ADMINISTRATION",Infos.GOODLOGIN, Infos.GOODPASSWORD).code

        assert (200 == ProjectAPI.show(project.id,user.username,"password").code)
        assert (200 == ProjectAPI.update(project.id,project.encodeAsJSON(),user.username,"password").code)

        def result = AclAPI.delete(project.class.name, project.id, user.id,"ADMINISTRATION",Infos.GOODLOGIN, Infos.GOODPASSWORD)
        assert 200 == result.code
        def json = JSON.parse(result.data)

        assert (200 == ProjectAPI.show(project.id,user.username,"password").code)
        assert (403 == ProjectAPI.update(project.id,project.encodeAsJSON(),user.username,"password").code)

        result = AclAPI.delete(project.class.name, project.id, user.id,"READ",Infos.GOODLOGIN, Infos.GOODPASSWORD)
        assert 200 == result.code
        json = JSON.parse(result.data)

        assert (403 == ProjectAPI.show(project.id,user.username,"password").code)
        assert (403 == ProjectAPI.update(project.id,project.encodeAsJSON(),user.username,"password").code)

    }

    void testDeleteACLObjectNotFound() {
         User user = BasicInstanceBuilder.getUserNotExist(true)
         Project project = BasicInstanceBuilder.getProjectNotExist(true)

         def result = AclAPI.delete(project.class.name, 1, user.id,"ADMINISTRATION",Infos.GOODLOGIN, Infos.GOODPASSWORD)
         assert 200 == result.code
         def json = JSON.parse(result.data)

    }

    void testCriticalTestRestrictACL() {
        User user = BasicInstanceBuilder.getUserNotExist(true)
        Project project = BasicInstanceBuilder.getProjectNotExist(true)

        assert (403 == ProjectAPI.show(project.id,user.username,"password").code)
        assert (403 == ProjectAPI.update(project.id,project.encodeAsJSON(),user.username,"password").code)

        def result = AclAPI.create(project.class.name, project.id, user.id,"READ",user.username, "password")
        assert 403 == result.code

        assert (403 == ProjectAPI.show(project.id,user.username,"password").code)
        assert (403 == ProjectAPI.update(project.id,project.encodeAsJSON(),user.username,"password").code)

        assert 200 == AclAPI.create(project.class.name, project.id, user.id,"READ",Infos.GOODLOGIN, Infos.GOODPASSWORD).code
        assert 200 == AclAPI.create(project.class.name, project.id, user.id,"ADMINISTRATION",Infos.GOODLOGIN, Infos.GOODPASSWORD).code

        result = AclAPI.delete(project.class.name, project.id, user.id,"READ",user.username, "password")
        assert 403 == result.code

    }

}
