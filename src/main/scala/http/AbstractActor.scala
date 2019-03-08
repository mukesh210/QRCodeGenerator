package http

import akka.actor.{Actor, ActorSystem}
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.typesafe.config.{Config, ConfigFactory}
import scala.concurrent.duration.SECONDS

abstract class AbstractActor extends Actor {
  implicit val config: Config = ConfigFactory.load()
  implicit val actorSystem: ActorSystem = ActorSystem("web-app")
  implicit val materialize: ActorMaterializer = ActorMaterializer()
  implicit val timeout: Timeout = Timeout(config.getInt("askTimeout"), SECONDS)
}
