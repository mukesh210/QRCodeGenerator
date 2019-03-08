package repo

import com.datastax.driver.core._
import model.Implicits._

import scala.concurrent.Future

trait Repository extends Binder {
  def checkIfKioskIdPresent(kioskId: String): Future[Boolean] = {
    val boundStatement: BoundStatement = getKiosk(kioskId)
    val result: Future[ResultSet] = session.executeAsync(boundStatement).asScala
    result.map(_.isExhausted)
  }

  def saveSessionInDB(sessionId: String, userId: String, kioskId: String): Future[Boolean] = {
    val boundStatement = getSaveSessionBs(sessionId, userId, kioskId)
    val rs = session.executeAsync(boundStatement).asScala
    rs.map(_.wasApplied())
  }
}
