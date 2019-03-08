import akka.http.scaladsl.server.Route
import db.DBConfigurations
import service.Service

object boot extends App {
  private val session = new DBConfigurations().getSession

  val service: Route = Service(session)
}
