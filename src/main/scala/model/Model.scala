package model

case class KioskInfo(kioskId: String)

case class KioskInfoUser(kioskInfo: String, userId: String)

case class KioskUserId(kioskId: String, userId: String)

case class KioskUserQrInfo(kioskUserQrInfo: String)