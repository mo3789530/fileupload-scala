package controllers

import akka.stream.scaladsl.FileIO
import play.api.http.HttpEntity
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, BaseController, ControllerComponents}

import java.nio.file.Paths
import javax.inject.{Inject, Singleton}
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

@Singleton
class FileController @Inject()(val controllerComponents: ControllerComponents,
                               val ws: WSClient) extends BaseController{
  def upload() = Action(parse.multipartFormData) { request =>
    request.body.file("file")
      .map{
      file =>
        print("upload")
        val fileName = Paths.get(file.filename).getFileName
        FileIO.fromPath(file.ref.path)
        Console.println(file.fileSize.toString)
        try {
          val wsResponse = ws.url("http://192.168.11.11:9090/test/hoge.txt")
            .addHttpHeaders("Date" -> "Sat, 26 Feb 2022 22:21:16 +0900")
            .addHttpHeaders("Authorization" -> "AWS ")
            .addHttpHeaders("Content-Type" -> "application/octet-stream")
            .addHttpHeaders("Content-Length" -> file.fileSize.toString)
            .addHttpHeaders("HOST"-> "192.168.11.11:9090")
            .withBody(FileIO.fromPath(file.ref.path))
            .execute("PUT")
          val res = Await.result(wsResponse, Duration.Inf)
          print(res.status)
        }
        catch {
          case e: Exception => Console.println(e)
        }

        Ok("File uploaded")
      }
      .getOrElse {
        Ok("File uploaded")
      }

  }

  def uploadToStorage() = {

  }


  def download() = Action.async {
    ws.url("http://192.168.11.11:9090/test/testfile.txt").withMethod("GET")
      .addHttpHeaders("Date" -> "Sun, 27 Feb 2022 12:17:36 +0900")
      .addHttpHeaders("Authorization" -> " AWS ")
      .addHttpHeaders("Content-Type" -> "application/zstd")
      .addHttpHeaders("HOST" -> "192.168.11.11:9090")
      .stream()
      .map { response =>
        if (response.status == 200) {
          // Get the content type
          val contentType = response.headers
            .get("Content-Type")
            .flatMap(_.headOption)
            .getOrElse("application/octet-stream")

          // If there's a content length, send that, otherwise return the body chunked
          response.headers.get("Content-Length") match {
            case Some(Seq(length)) =>
              Ok.sendEntity(HttpEntity.Streamed(response.bodyAsSource, Some(length.toLong), Some(contentType)))
            case _ =>
              Ok.chunked(response.bodyAsSource).as(contentType)
          }
        }
        else {
          Console.println("Bad response")
          BadGateway
        }
      }
  }
}
