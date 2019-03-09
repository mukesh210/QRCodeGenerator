package constants
import Constants._
object QueryConstants {
  val GET_KIOSK = s"select $KIOSK_ID from $KEYSPACE_NAME.$KIOSK_TABLE_NAME where $KIOSK_ID=:$KIOSK_ID"

  val INSERT_SESSION = s"insert into $KEYSPACE_NAME.$SESSION_TABLE_NAME($SESSION_ID, $EMAIL, $KIOSK_ID) values(" +
    s":$SESSION_ID, :$EMAIL, :$KIOSK_ID) USING TTL 300"
}
