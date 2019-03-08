package service

import com.datastax.driver.core.Session
import akka.http.scaladsl.model.{HttpResponse, Multipart, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Route, StandardRoute}
import com.datastax.driver.core.utils.UUIDs
import com.google.gson.Gson
import model.{KioskInfoUser, KioskUserQrInfo}
import util.Helper
import constants.Constants._

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

class Service(ses: Session) extends Helper {
  override val session: Session = ses
  override val gson: Gson = new Gson()
  override val genKey: String = "klix8TW3SMGtHLVO0ZbhwO8ggW0p+npHfB71epkvmE0="

  val routes: Route = getKioskQR ~ validateKiosk ~ getSession

  val getKioskQR: Route = path("/getKioskQR") {
    get {
      parameters('kioskId.as[String]) { kioskId =>
        getResponseForQRGeneration(encryptAndGenerateQR(kioskId))
      }
    }
  }

  val validateKiosk: Route = path("/validateKiosk") {
    post {
      entity(as[KioskInfoUser]) {kioskInfoUser =>
        onComplete(validateKioskInfoGenerateQR(kioskInfoUser)) {response: Try[String] =>
          processValidateKiosk(response)
        }
      }
    }
  }

  val getSession: Route = path("/getSession") {
    post {
      entity(as[KioskUserQrInfo]) {kioskUserQrInfo =>
        val sessionId = UUIDs.timeBased().toString
        onComplete(saveSession(sessionId, kioskUserQrInfo)) {response: Try[Boolean] =>
          processGetSessionResponse(response, sessionId)
        }
      }
    }
  }

  def processGetSessionResponse(response: Try[Boolean], sessionId: String) = complete {
    response match {
      case Success(result: Boolean) =>
        if(result)
          HttpResponse(status = StatusCodes.OK, entity = sessionId)
        else
          HttpResponse(status = StatusCodes.InternalServerError, entity = "Session Not established")

      case Failure(ex) =>
        HttpResponse(status = StatusCodes.InternalServerError, entity = ex.getMessage)
    }
  }

  def processValidateKiosk(eventualString: Try[String]): StandardRoute = complete {
    eventualString match {
      case Success(result: String) =>
        if(result == INVALID_KIOSK_ID)
          HttpResponse(status = StatusCodes.BadRequest, entity = result)
        else HttpResponse(status = StatusCodes.OK, entity = result)

      case Failure(ex) =>
        HttpResponse(status = StatusCodes.InternalServerError, entity = ex.getMessage)
    }
  }

  def getResponseForQRGeneration(response: String) = complete {
    HttpResponse(status = StatusCodes.OK, entity = response)
  }

}

object Service {
  def apply(session: Session): Route = new Service(session).routes
}