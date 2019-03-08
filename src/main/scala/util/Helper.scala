package util

import java.nio.file.Files
import java.util.Base64

import com.google.gson.Gson
import model.{KioskInfo, KioskInfoUser, KioskUserId, KioskUserQrInfo}
import net.glxn.qrgen.QRCode
import repo.{Binder, Repository}
import constants.Constants._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait Helper extends Repository {
  val genKey: String
  val gson: Gson

  def encryptData(data: String): String = {
    val encrypted: Array[Byte] = AES.encrypt(data.toString.getBytes("UTF-8"), genKey)
    AES.encodeBase64(encrypted)
  }

  def decryptData(data: String): String = {
    val bytesArray: Array[Byte] = AES.decodeBase64(data)
    val finalData: Array[Byte] = AES.decrypt(bytesArray, genKey)

    new String(finalData)
  }

  def getQRCodeFromData(data: String): String = {
    val f = QRCode.from(data).withCharset("UTF-8").file()
    val output: Array[Byte] = Files.readAllBytes(f.toPath)
    val encodedBytes: Array[Byte] = Base64.getEncoder().encode(output)
    new String(encodedBytes)
  }

  def encryptAndGenerateQR(kioskId: String) = {
    val kioskInfo = KioskInfo(kioskId)
    val jsonData: String = gson.toJson(kioskInfo)
    val encryptedJson = encryptData(jsonData)
    getQRCodeFromData(encryptedJson)
  }

  def validateKioskInfoGenerateQR(kioskInfoUser: KioskInfoUser): Future[String] = {
    val kioskInfo = kioskInfoUser.kioskInfo
    val userId = kioskInfoUser.userId
    val decryptedKioskData = decryptData(kioskInfo)
    val kioskInfoClass: KioskInfo = gson.fromJson(decryptedKioskData, classOf[KioskInfo])
    checkIfKioskIdPresent(kioskInfoClass.kioskId).map(x => {
      if(x){
        val kioskUserId = KioskUserId(kioskInfoClass.kioskId, userId)
        val kioskUserIdJson: String = gson.toJson(kioskUserId)
        val kioskUserIdJsonQR: String = getQRCodeFromData(kioskUserIdJson)
        val kioskUserQRInfo = KioskUserQrInfo(kioskUserIdJsonQR)
        gson.toJson(kioskUserQRInfo)
      } else INVALID_KIOSK_ID
    })
  }

  def saveSession(sessionId: String, kioskUserQrInfo: KioskUserQrInfo): Future[Boolean] = {
    val decryptedData: String = decryptData(kioskUserQrInfo.kioskUserQrInfo)
    val kioskUserId = gson.fromJson(decryptedData, classOf[KioskUserId])
    saveSessionInDB(sessionId, kioskUserId.userId, kioskUserId.kioskId)
  }

}
