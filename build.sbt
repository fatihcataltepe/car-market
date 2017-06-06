lazy val root = (project in file(".")).enablePlugins(JavaAppPackaging)

name := "car-market"

organization := "com.scout24"

version := "0.0.1"

scalaVersion := "2.11.8"

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

resolvers += Resolver.sonatypeRepo("snapshots")

mappings in Universal += {
  val conf = (resourceDirectory in Test).value / "logback.xml"
  conf -> "conf/logback.xml"
}

mappings in Universal += {
  val conf = (resourceDirectory in Test).value / "application.conf"
  conf -> "conf/application.conf"
}

mappings in Universal += {
  file("readme.md") -> "readme.md"
}


//scala logging
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.7"

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.4.0"

//typesafe config
libraryDependencies += "com.typesafe" % "config" % "1.3.0"

//slick
libraryDependencies ++= List(
  "com.typesafe.slick" %% "slick" % "3.0.0",
  "mysql" % "mysql-connector-java" % "5.1.39"
)

//akka=http
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.0.6",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.3",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.0.6" % Test
)


//scala test library
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.0" % "test"