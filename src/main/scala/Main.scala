import scala.util._
import scala.concurrent.Await
import scala.concurrent.duration.Duration

import akka.actor._
import akka.stream._
import akka.http._
import akka.http.scaladsl._
import akka.http.scaladsl.model._
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.Directives._

object Main extends App {
  implicit val system = ActorSystem("mySystem")
  implicit val materializer = ActorMaterializer() 
  implicit val ec = system.dispatcher

  val route = get {
    path("time") {
      onComplete(Http().singleRequest(HttpRequest(uri = "http://localhost:28080/time"))) {
        case Success(_) =>
          complete(StatusCodes.OK)
        case Failure(e) => 
          system.log.error(e, "singleRequest failed")
          complete(StatusCodes.BadGateway)
      }
    }
  }

  Http().bindAndHandle(route, "localhost", 8080)

  Await.result(system.whenTerminated, Duration.Inf)
}
