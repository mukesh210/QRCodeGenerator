package model
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, DeserializationException, JsArray, JsNumber, JsObject, JsString, JsValue, JsonFormat, JsonWriter, RootJsonFormat}

case class KioskInfo(kioskId: String)

case class KioskInfoUser(kioskInfo: String, userId: String)

case class KioskUserId(kioskId: String, userId: String)

case class KioskUserQrInfo(kioskUserQrInfo: String)

trait WebRequestJsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val kioskInfoUserFormat: RootJsonFormat[KioskInfoUser] = jsonFormat2(KioskInfoUser)
  implicit val kioskUserQrInfoFormat: RootJsonFormat[KioskUserQrInfo] = jsonFormat1(KioskUserQrInfo)
}