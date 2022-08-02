import sbt.Keys._
import sbt._

object Dependencies {

  //------------------------------------------------------------------------------------------------------------
  // io.strongtyped.funcqrs core
  val scalaLogging    = "com.typesafe.scala-logging" %% "scala-logging"   % "3.9.2"
  val scalaTest       = "org.scalatest"              %% "scalatest"       % "3.2.0" % "test"
  val logback         = "ch.qos.logback"             % "logback-classic"  % "1.2.3"
  val reactiveStreams = "org.reactivestreams"        % "reactive-streams" % "1.0.3"

  val rxScala               = "io.reactivex" %% "rxscala"                % "0.27.0"
  val reactiveStreamAdapter = "io.reactivex" % "rxjava-reactive-streams" % "1.2.1"

  val mainDeps = Seq(scalaLogging, scalaTest, logback, reactiveStreams)
  //------------------------------------------------------------------------------------------------------------

  //------------------------------------------------------------------------------------------------------------
  // Akka Module
  val akkaDeps = {
    val akkaVersion = "2.6.5"

    Seq(
      "com.typesafe.akka" %% "akka-actor"       % akkaVersion,
      "com.typesafe.akka" %% "akka-persistence" % akkaVersion,
      "com.typesafe.akka" %% "akka-slf4j"       % akkaVersion,
      "com.typesafe.akka" %% "akka-stream"      % akkaVersion,
      
      // experimental
      "com.typesafe.akka" %% "akka-persistence-query" % akkaVersion,
      // test scope
      "com.typesafe.akka"   %% "akka-testkit"              % akkaVersion % "test",
      "com.github.dnvriend" %% "akka-persistence-inmemory" % "2.5.15.2"  % "test",

      //"com.typesafe.akka" %% "akka-persistence-dynamodb" % "1.2.0-RC2"
      "com.github.j5ik2o" %% "akka-persistence-dynamodb-query" % "1.2.3" withSources(),
      "com.github.j5ik2o" %% "akka-persistence-dynamodb-journal" % "1.2.3" withSources(),
      "com.github.j5ik2o" %% "akka-persistence-dynamodb-snapshot" % "1.2.3" withSources(),

      //"com.typesafe.akka" %% "akka-persistence-query" % akkaVersion
      //"com.typesafe.akka" %% "akka-persistence-typed" % akkaVersion,
      //"akka-serialization-jackson" % akkaVersion,
      //"com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test,
      //"com.typesafe.akka" %% "akka-remote" % akkaVersion
      //libraryDependencies += "com.typesafe.akka" %% "akka-serialization-jackson" % AkkaVersion
      "com.typesafe.akka" %% "akka-serialization-jackson" % akkaVersion

    )
  }
  //------------------------------------------------------------------------------------------------------------

  //------------------------------------------------------------------------------------------------------------
  //val levelDb    = "org.iq80.leveldb"          % "leveldb"        % "0.7"
  //val levelDbJNI = "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8"

  val sampleDeps = /*Seq(levelDb, levelDbJNI) ++*/ mainDeps ++ akkaDeps
}
