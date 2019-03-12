import java.io.InputStream
import java.security.{KeyStore, SecureRandom}

import akka.actor.ActorSystem
import akka.http.scaladsl.{ConnectionContext, Http, HttpsConnectionContext}
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.typesafe.sslconfig.akka.AkkaSSLConfig
import db.DBConfigurations
import http.AbstractActor
import javax.net.ssl.{KeyManagerFactory, SSLContext, TrustManagerFactory}
import javax.xml.ws.http.HTTPBinding
import service.Service

object boot extends AbstractActor with App {
  private val session = new DBConfigurations().getSession

  val sslConfig = AkkaSSLConfig(actorSystem)

  implicit val flowMaterializer = ActorMaterializer()

  val ks: KeyStore = KeyStore.getInstance("PKCS12")
  val keystore: InputStream = getClass.getClassLoader.getResourceAsStream("keystore.pkcs12")

  require(keystore != null, "Keystore required!")

  val password: Array[Char] = "mukesh".toCharArray // do not store passwords in code, read them from somewhere safe!
  ks.load(keystore, password)

  val keyManagerFactory: KeyManagerFactory = KeyManagerFactory.getInstance("SunX509")
  keyManagerFactory.init(ks, password)

  val tmf: TrustManagerFactory = TrustManagerFactory.getInstance("SunX509")
  tmf.init(ks)

  val sslContext: SSLContext = SSLContext.getInstance("TLS")
  sslContext.init(keyManagerFactory.getKeyManagers, tmf.getTrustManagers, new SecureRandom)
  val https: HttpsConnectionContext = ConnectionContext.https(sslContext)
  val route = Service(session)

  val httpsBinding = Http().bindAndHandle(route, "localhost", 8080, connectionContext = https)

  println(s"Server is now online at http://8080\n")
}
