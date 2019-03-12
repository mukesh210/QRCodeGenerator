package service

import java.io.File
import java.nio.file.{Files, Paths}

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Route, StandardRoute}
import com.datastax.driver.core.Session
import com.datastax.driver.core.utils.UUIDs
import com.google.gson.{Gson, JsonObject}
import model._
import util.Helper
import constants.Constants._
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._

import scala.concurrent.Future
import com.google.gson.Gson
import constants.Constants._
import util.Helper

import scala.util.{Failure, Success, Try}
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
import Directives._
import akka.http.scaladsl.server.directives.FileInfo
import akka.stream.scaladsl.FileIO

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
    } ~ path("changeBanner") {
      downloadFile
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
      onComplete(saveSession(sessionId, kioskUserQrInfo)) { response: Try[KioskUserSessionInfo] =>
        processGetSessionResponse(response)
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
      val modifiedBetData = betData.replaceAll(" ", "+")
      val response = getBetDataFromQRCode(modifiedBetData)
      processQRCodeForBet(response)
    }
  }


  def tempDestination(fileInfo: FileInfo): File =
    new File("/tmp", fileInfo.fileName)


  val downloadFile: Route = post {
    storeUploadedFile("csv", tempDestination) {
      case (metadata, file) =>
        println(s"metadata: ${metadata} file:${file}")
        // do something with the file and file metadata ...
        Thread.sleep(2000)
        //file.delete()
        complete(StatusCodes.OK)
    }
  }

  def processQRCodeForBet(response: String): StandardRoute = complete {
    HttpResponse(status = StatusCodes.OK, entity=response)
  }

  def processGetSessionResponse(response: Try[KioskUserSessionInfo]) = complete {
    response match {
      case Success(result: KioskUserSessionInfo) =>
        if (result.sessionId != "")
          HttpResponse(status = StatusCodes.OK, entity = gson.toJson(result))
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