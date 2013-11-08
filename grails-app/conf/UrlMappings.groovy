import be.cytomine.Exception.ForbiddenException
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.acls.model.NotFoundException

class UrlMappings {

    static mappings = {     //?(.$format)?
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }
        "/admin/manage/$action?"(controller: "adminManage")
        "/adminManage/$action?"(controller: "errors", action: "error500")

        /* Home */
        "/"(view:"/index")

        /* Errors */
//        "/500" (view:'/error')
        //        "/403" (view:'/forbidden')
        "403"(controller: "errors", action: "error403")
        //"404"(controller: "errors", action: "error404")
        "500"(controller: "errors", action: "error500")
        "500"(controller: "errors", action: "error403", exception: AccessDeniedException)
        "500"(controller: "errors", action: "error403", exception: NotFoundException)
        "500"(controller: "errors", action: "error403", exception: ForbiddenException)
 //       "500"(controller: "errors", action: "error404", exception: ObjectNotFoundException)

        "/processing/detect/$image/$x/$y"(controller:"processing") {
            action = [GET : "detect"]
        }
        "/processing/show/$image/$x/$y"(controller:"processing") {
            action = [GET : "show"]
        }
        "/api/import/imageproperties"(controller: "import") {
            action = [GET:"imageproperties"]
        }
        "/api/export/exportimages"(controller: "export") {
            action = [GET:"exportimages"]
        }

        "/api/project/$id/commandhistory"(controller: "restProject") {
            action = [GET:"listCommandHistory"]
        }
        "/api/commandhistory"(controller: "restProject") {
            action = [GET:"listCommandHistory"]
        }

        "/api/search"(controller: "search") {
            action = [GET:"listResponse"]
        }

        "/api/news"(controller:"news") {
            action = [GET:"listNews"]
        }

        "/loginWithoutLDAP/login" (controller: "login") {
            action = [GET:"loginWithoutLDAP"]
        }
    }
}
