libraryDependencies ++= Seq(
  "com.typesafe" %% "play-mini" % "2.0.3",
  "com.typesafe.akka" % "akka-actor" % "2.0.4"
)

mainClass in (Compile, run) := Some("play.core.server.NettyServer")
