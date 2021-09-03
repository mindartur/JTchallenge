/*
 * Copyright (c) 2020 Contributors as noted in the AUTHORS.md file
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.challenge.jtchallenge

import java.util.concurrent.{ ExecutorService, Executors }

import cats.effect._
import cats.implicits._
import com.typesafe.config._
import com.challenge.jtchallenge.api._
import com.challenge.jtchallenge.config._
import com.challenge.jtchallenge.db.FlywayDatabaseMigrator
import eu.timepit.refined.auto._
import org.http4s.ember.server._
import org.http4s.implicits._
import org.http4s.server.Router
import org.slf4j.LoggerFactory
import pureconfig._
import sttp.tapir.docs.openapi._
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.swagger.http4s.SwaggerHttp4s

import scala.concurrent.ExecutionContext

object Server extends IOApp.WithContext {
  val blockingPool: ExecutorService = Executors.newFixedThreadPool(2)
  val ec: ExecutionContext          = ExecutionContext.global

  val log = LoggerFactory.getLogger(Server.getClass())

  override protected def executionContextResource: Resource[SyncIO, ExecutionContext] = Resource.eval(SyncIO(ec))

  override def run(args: List[String]): IO[ExitCode] = {
    val blocker  = Blocker.liftExecutorService(blockingPool)
    val migrator = new FlywayDatabaseMigrator

    val program = for {
      config <- IO(ConfigFactory.load(getClass().getClassLoader()))
      dbConfig <- IO(
        ConfigSource.fromConfig(config).at(DatabaseConfig.CONFIG_KEY).loadOrThrow[DatabaseConfig]
      )
      serviceConfig <- IO(
        ConfigSource.fromConfig(config).at(ServiceConfig.CONFIG_KEY).loadOrThrow[ServiceConfig]
      )
      _ <- migrator.migrate(dbConfig.url, dbConfig.user, dbConfig.pass)
      helloWorldRoutes = new HelloWorld[IO]
      docs             = OpenAPIDocsInterpreter().toOpenAPI(List(HelloWorld.greetings), "JTchallenge", "1.0.0")
      swagger          = new SwaggerHttp4s(docs.toYaml)
      routes           = helloWorldRoutes.routes <+> swagger.routes[IO]
      httpApp          = Router("/" -> routes).orNotFound
      resource = EmberServerBuilder
        .default[IO]
        .withBlocker(blocker)
        .withHost(serviceConfig.ip)
        .withPort(serviceConfig.port)
        .withHttpApp(httpApp)
        .build
      fiber = resource.use(server =>
        IO.delay(log.info("Server started at {}", server.address)) >> IO.never.as(ExitCode.Success)
      )
    } yield fiber
    program.attempt.unsafeRunSync() match {
      case Left(e) =>
        IO {
          log.error("An error occured during execution!", e)
          ExitCode.Error
        }
      case Right(s) => s
    }
  }

}
