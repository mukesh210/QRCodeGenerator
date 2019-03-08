package repo

import java.sql.PreparedStatement

import com.datastax.driver.core.Session
import constants.QueryConstants
import constants.Constants._
import model.KioskUserId
trait Binder {
  val session: Session

  def getKiosk(kioskId: String) = {
    val preparedStatement = session.prepare(QueryConstants.GET_KIOSK)
    val boundsStatement = preparedStatement.bind()
    boundsStatement.setString(KIOSK_ID, kioskId)

    boundsStatement
  }

  def getSaveSessionBs(sessionId: String, userId: String, kioskId: String) = {
    val preparedStatement = session.prepare(QueryConstants.INSERT_SESSION)
    val boundStatement = preparedStatement.bind()
    boundStatement.setString(KIOSK_ID, kioskId)
    boundStatement.setString(USER_ID, userId)
    boundStatement.setString(SESSION_ID, sessionId)

    boundStatement
  }

}
