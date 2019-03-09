package constants
import Constants._
object QueryConstants {
  val GET_KIOSK = s"select $KIOSK_ID from $KEYSPACE_NAME.$KIOSK_TABLE_NAME where $KIOSK_ID=:$KIOSK_ID"

  val INSERT_SESSION = s"insert into $KEYSPACE_NAME.$SESSION_TABLE_NAME($USER_ID, $SESSION_ID, $KIOSK_ID) values(" +
    s":$EMAIL, :$SESSION_ID, :$KIOSK_ID)"
}
