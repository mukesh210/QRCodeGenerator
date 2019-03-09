package service

import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Route, StandardRoute}
import com.datastax.driver.core.Session
import com.datastax.driver.core.utils.UUIDs
import com.google.gson.{Gson, JsonObject}
import model.{KioskInfoUser, KioskUserQrInfo, WebRequestJsonSupport}
import util.Helper
import constants.Constants._
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._
import scala.concurrent.Future
import com.google.gson.Gson
import constants.Constants._
import model.{KioskInfoUser, KioskUserQrInfo, UserBetDetails, WebRequestJsonSupport}
import util.Helper
import scala.util.{Failure, Success, Try}
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
import Directives._

class Service(ses: Session) extends Helper with WebRequestJsonSupport {
  override val session: Session = ses
  override val gson: Gson = new Gson()
  override val genKey: String = "klix8TW3SMGtHLVO0ZbhwO8ggW0p+npHfB71epkvmE0="

  val routes: Route = cors() {
    path("getKioskQR") {
      getKioskQR
    } ~ path("validateKiosk") {
      validateKiosk
    } ~ path("getSession") {
      getSession
    } ~ path("generateBetQR") {
      generateBetQRCode
    } ~ path("getBetData") {
      getBetDataFromQR
    }
  }



  val getKioskQR: Route = get {
    parameters('kioskId.as[String]) { kioskId =>
      getResponseForQRGeneration(encryptAndGenerateQR(kioskId))
    }
  }


  val validateKiosk: Route = post {
    entity(as[KioskInfoUser]) { kioskInfoUser =>
      onComplete(validateKioskInfoGenerateQR(kioskInfoUser)) { response: Try[String] =>
        processValidateKiosk(response)
      }
    }
  }

  val getSession: Route = post {
    entity(as[KioskUserQrInfo]) { kioskUserQrInfo =>
      val sessionId = UUIDs.timeBased().toString
      onComplete(saveSession(sessionId, kioskUserQrInfo)) { response: Try[Boolean] =>
        processGetSessionResponse(response, sessionId)
      }
    }
  }

  val generateBetQRCode: Route = post {
    entity(as[UserBetDetails]) { userBetDetails =>
      val response = generateQRCodeForBet(userBetDetails)
      processQRCodeForBet(response)
    }
  }

  val getBetDataFromQR: Route = get {
    parameters('betData.as[String]) { betData =>
      val response = getBetDataFromQRCode(betData)
      processQRCodeForBet(response)
    }
  }

  def processQRCodeForBet(response: String): StandardRoute = complete {
    HttpResponse(status = StatusCodes.OK, entity=response)
  }

  def processGetSessionResponse(response: Try[Boolean], sessionId: String) = complete {
    response match {
      case Success(result: Boolean) =>
        if (result)
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
        if (result == INVALID_KIOSK_ID)
          HttpResponse(status = StatusCodes.BadRequest, entity = result)
        else HttpResponse(status = StatusCodes.OK, entity = result)

      case Failure(ex) =>
        HttpResponse(status = StatusCodes.InternalServerError, entity = ex.getMessage)
    }
  }

  def getResponseForQRGeneration(response: String) = complete {
//    val jsonObject = new JsonObject()
//    jsonObject.addProperty("result", response)
//    HttpResponse(status = StatusCodes.OK, entity = jsonObject.toString)
    HttpResponse(status = StatusCodes.OK, entity = response)
  }

}

object Service {
  def apply(session: Session): Route = new Service(session).routes
}