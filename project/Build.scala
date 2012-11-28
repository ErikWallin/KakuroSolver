import sbt._
import Keys._

object KakuroSolver20 extends Build {
    lazy val root = Project(id = "kakurosolver", base = file(".")) aggregate(domain, akka, rest)
    lazy val domain = Project(id = "kakurosolver-domain", base = file("domain"))
    lazy val akka = Project(id = "kakurosolver-akka", base = file("akka")) dependsOn(domain)
    lazy val rest = Project(id = "kakurosolver-rest", base = file("rest")) dependsOn(domain, akka) 
}

