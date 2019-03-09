package model
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, DeserializationException, JsArray, JsNumber, JsObject, JsString, JsValue, JsonFormat, JsonWriter, RootJsonFormat}

case class KioskInfo(kioskId: String)

case class KioskInfoUser(kioskInfo: String, email: String)

case class KioskUserId(kioskId: String, email: String)

case class KioskUserQrInfo(kioskUserQrInfo: String)

case class KioskUserSessionInfo(kioskId: String, email: String, sessionId: String)

case class UserBetDetails(betId: String, email: String, kioskId: String, matchId: String, amountDue: String)

trait WebRequestJsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val kioskInfoUserFormat: RootJsonFormat[KioskInfoUser] = jsonFormat2(KioskInfoUser)
  implicit val kioskUserQrInfoFormat: RootJsonFormat[KioskUserQrInfo] = jsonFormat1(KioskUserQrInfo)
  implicit val userBetDetailsFormat: RootJsonFormat[UserBetDetails] = jsonFormat5(UserBetDetails)
  implicit val kioskUserSessionInfoFormat: RootJsonFormat[KioskUserSessionInfo] = jsonFormat3(KioskUserSessionInfo)
}