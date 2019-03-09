name := "Makeathon4"

version := "0.1"

scalaVersion := "2.12.8"

// https://mvnrepository.com/artifact/net.glxn/qrgen
libraryDependencies += "net.glxn" % "qrgen" % "1.4"

// https://mvnrepository.com/artifact/commons-codec/commons-codec
libraryDependencies += "commons-codec" % "commons-codec" % "1.9"

// https://mvnrepository.com/artifact/com.google.code.gson/gson
libraryDependencies += "com.google.code.gson" % "gson" % "2.8.5"

// https://mvnrepository.com/artifact/com.datastax.cassandra/cassandra-driver-core
libraryDependencies += "com.datastax.cassandra" % "cassandra-driver-core" % "3.6.0"

// https://mvnrepository.com/artifact/com.typesafe.akka/akka-http
libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.1.7"

libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.5.19"

// https://mvnrepository.com/artifact/io.spray/spray-json
libraryDependencies += "io.spray" %% "spray-json" % "1.3.5"

libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.7"

libraryDependencies += "ch.megard" %% "akka-http-cors" % "0.3.4"
