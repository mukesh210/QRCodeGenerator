package db

import com.datastax.driver.core.{Cluster, ConsistencyLevel, QueryOptions, Session}
import com.typesafe.config.{Config, ConfigFactory}
import constants.Constants._

/**
  *
  * created by raman mishra 15-Nov-2018
  */
class DBConfigurations {
  private val config: Config = ConfigFactory.load()
  private val uri: String = config.getString(CASSANDRA_URI)
  private val path = uri.split("//")(1).split(":")
  private val host = path(HOST_INDEX)
  private val port = path(PORT_INDEX).split("/")(0).toInt
  private val cluster = new Cluster.Builder()
    .addContactPoint(host).withPort(port)
    .withQueryOptions(new QueryOptions().setConsistencyLevel(ConsistencyLevel.LOCAL_ONE)).build()

  /**
    * method to create session
    *
    * @return session
    */
  def getSession: Session = {
    cluster.connect()
  }
}
