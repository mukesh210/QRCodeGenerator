import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import db.DBConfigurations
import http.AbstractActor
import javax.xml.ws.http.HTTPBinding
import service.Service

object boot extends AbstractActor with App {
  private val session = new DBConfigurations().getSession

  val service: Route = Service(session)

  Http().bindAndHandle(service, "localhost", 8080)
}
