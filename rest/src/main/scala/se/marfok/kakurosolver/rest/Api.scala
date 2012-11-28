package se.marfok.kakurosolver.rest

import com.typesafe.config.ConfigFactory
import com.typesafe.play.mini.{ Application, GET, POST, Path }
import akka.actor.{ ActorSystem, Props }
import akka.pattern.ask
import akka.util.duration.intToDurationInt
import akka.util.Timeout
import play.api.libs.concurrent.akkaToPlay
import play.api.mvc.Results.Ok
import play.api.mvc.Results.BadRequest
import play.api.mvc.{ Action, AsyncResult }
import se.marfok.kakurosolver.akka.{ PuzzleActor, Solve }
import se.marfok.kakurosolver.domain.Puzzle
import play.api.data.Form
import play.api.data.Forms.text
import play.api.libs.json.Json.parse
import play.api.libs.json.Json._
import com.typesafe.play.mini.Routes
import com.typesafe.play.mini.Through

/**
 * The REST api to KakuroSolver
 */
object Api extends Application {

  val config = ConfigFactory.load()
  val system = ActorSystem("KakuroSolver", config)
  implicit val timeout = Timeout(60 seconds)

  def route = {
    case GET(Path("/hello")) => Action {
      Ok("<h1>Hello world!<h1>").as("text/html")
    }
    case POST(Path("/kakuro")) => Action { implicit request =>
      val json = Form("json" -> text).bindFromRequest.get
      AsyncResult {
        val puzzle = Puzzle.fromJson(json)
        val actor = system.actorOf(Props(new PuzzleActor(puzzle)))
        (actor ? Solve).mapTo[Puzzle].asPromise.map { result ⇒
          Ok(result.toJson)
        }
      }
    }
  }
}
