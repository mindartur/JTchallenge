/*
 * Copyright (c) 2021 Artur
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.challenge.jtchallenge

import java.util.concurrent.{ExecutorService, Executors}
import cats.effect._
import cats.implicits._
import com.typesafe.config._
import com.challenge.jtchallenge.api._
import com.challenge.jtchallenge.config._
import com.challenge.jtchallenge.services.{ConnectionsServiceImpl, GitHubServiceImpl, TwitterServiceImpl}
import eu.timepit.refined.auto._
import com.comcast.ip4s.{Host, Port}
import org.http4s.client.Client
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.ember.server._
import org.http4s.implicits._
import org.http4s.server.Router
import org.slf4j.LoggerFactory
import pureconfig._
import sttp.tapir.docs.openapi._
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.swagger.http4s.SwaggerHttp4s


object Server extends IOApp {
  val blockingPool: ExecutorService = Executors.newFixedThreadPool(2)
//  implicit val runtime1 = cats.effect.unsafe.IORuntime.global

  val log = LoggerFactory.getLogger(Server.getClass())

  override def run(args: List[String]): IO[ExitCode] = {
    val httpClientResource = EmberClientBuilder.default[IO].build

    val program = for {
      config <- IO(ConfigFactory.load(getClass().getClassLoader()))
      serviceConfig <- IO(
        ConfigSource.fromConfig(config).at(ServiceConfig.CONFIG_KEY).loadOrThrow[ServiceConfig]
      )
      githubConfig <- IO(
        ConfigSource.fromConfig(config).at(GithubConfig.CONFIG_KEY).loadOrThrow[GithubConfig]
      )
      twitterService <- IO(new TwitterServiceImpl())
      githubService <- httpClientResource.use((httpClient: Client[IO]) =>  IO(new GitHubServiceImpl(githubConfig)(implicitly(httpClient))))
      connectionsService <- IO(new ConnectionsServiceImpl(twitterService, githubService))
      developersRoutes = new DevelopersRoutes(connectionsService)
      docs             = OpenAPIDocsInterpreter().toOpenAPI(List(DevelopersRoutes.connectedRoute), "JTchallenge", "1.0.0")
      swagger          = new SwaggerHttp4s(docs.toYaml)
      routes           = developersRoutes.routes <+> swagger.routes[IO]
      httpApp          = Router("/" -> routes).orNotFound
      resource = EmberServerBuilder
        .default[IO]
        .withHost(Host.fromString(serviceConfig.ip).get)
        .withPort(Port.fromInt(serviceConfig.port).get)
        .withHttpApp(httpApp)
        .build
      fiber = resource.use(server =>
        IO.delay(log.info("Server started at {}", server.address)) >> IO.never.as(ExitCode.Success)
      )
    } yield fiber
    program.attempt.unsafeRunSync()(implicitly(runtime)) match {
      case Left(e) =>
        IO {
          log.error("An error occured during execution!", e)
          ExitCode.Error
        }
      case Right(s) => s
    }
  }

}
