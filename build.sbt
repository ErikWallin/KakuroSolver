version := "0.1-SNAPSHOT"

scalaVersion in ThisBuild := "2.9.2"

scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation")

resolvers in ThisBuild += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies in ThisBuild ++= Seq(
  "org.specs2" %% "specs2" % "1.12.2" % "test",
  "junit" % "junit" % "4.8" % "test",
  "org.scalatest" %% "scalatest" % "1.8" % "test"
)

fork := true

