package com.guizmaii.code.kata.clients

import com.guizmaii.code.kata.types.IpAddress
import sttp.client3.*
import sttp.client3.ziojson.asJson
import sttp.model.HeaderNames
import zio.json.{DeriveJsonDecoder, JsonDecoder}
import zio.{Task, ZIO, ZLayer}

/**
 * Data structure representing the response of the IPify API
 */
private[clients] final case class PublicIPResponse(ip: IpAddress)
private[clients] object PublicIPResponse {
  implicit val decoder: JsonDecoder[PublicIPResponse] = DeriveJsonDecoder.gen[PublicIPResponse]
}

trait IpifyClient {
  def fetchMyPublicIP: Task[IpAddress]
}

object IpifyClient {
  val live: ZLayer[SttpBackend[Task, Any], Nothing, IpifyClient] =
    ZLayer.fromFunction(new IpifyClientLive(_))
}

final class IpifyClientLive(sttpBackend: SttpBackend[Task, Any]) extends IpifyClient {

  override val fetchMyPublicIP: Task[IpAddress] = {
    val request =
      basicRequest
        .get(uri"https://api.ipify.org/?format=json")
        .response(asJson[PublicIPResponse])

    request
      .send(sttpBackend)
      .flatMap(response => ZIO.fromEither(response.body.map(_.ip)))
      .logError(
        s"""
           |Call to IPify API to fetch public IP failed.
           |
           |Request:
           |```bash
           |${request.toCurl(sensitiveHeaders = HeaderNames.SensitiveHeaders)}
           |```
           |""".stripMargin.trim
      )
  }

}
