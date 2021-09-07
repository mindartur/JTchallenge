/*
 * Copyright (c) 2021 Artur
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.challenge.jtchallenge.api

import cats.effect.kernel.Async
import cats.effect.{Concurrent, IO}
import cats.implicits._
import com.challenge.jtchallenge.dto.{ConnectedFalseOutputDto, ConnectedInputDto, ConnectedOutputDto, ConnectedTrueOutputDto, ErrorsOutputDto, ServiceError}
import com.challenge.jtchallenge.models.Developer
import com.challenge.jtchallenge.services.ConnectionsService
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import sttp.tapir.path
import sttp.tapir.server.http4s.{Http4sServerInterpreter, Http4sServerOptions}
import sttp.tapir.{Endpoint, endpoint}
import eu.timepit.refined.auto._
import eu.timepit.refined.types.string.NonEmptyString
import sttp.tapir._
import sttp.tapir.codec.refined._
import sttp.tapir.json.circe._
import sttp.tapir.generic.auto._

final class DevelopersRoutes(connectionsService: ConnectionsService)
                            (implicit conc: Concurrent[IO], async: Async[IO])
  extends Http4sDsl[IO] {

  val routes: HttpRoutes[IO] = Http4sServerInterpreter[IO].toRoutes(DevelopersRoutes.connectedRoute) { connectedInputDto =>
    val (dev1, dev2) = (Developer(connectedInputDto.dev1), Developer(connectedInputDto.dev2))
    connectionsService
      .areConnected(dev1,dev2)
      .map(either =>
        either.bimap(errors => ErrorsOutputDto(errors.map(x => ServiceError.unsafeFrom(x)).toList), x => x)
      )
  }

}

object DevelopersRoutes {

  val ROOT = "developers"

  val exampleConnectedTrueOutputDto = ConnectedTrueOutputDto(
    List("org1", "org2")
  )
  val exampleConnectedFalseOutputDto = ConnectedFalseOutputDto(
  )

  val exampleErrors = ErrorsOutputDto(List(
    "dev1 is no a valid user in github",
    "dev1 is no a valid user in twitter",
    "dev2 is no a valid user in twitter"
  ))

  val connectedRoute: Endpoint[ConnectedInputDto, ErrorsOutputDto, ConnectedOutputDto, Any] = endpoint.get
    .prependIn(ROOT)
    .in("connected")
    .in(path[NonEmptyString]("dev1").and(path[NonEmptyString]("dev2")).mapTo[ConnectedInputDto])
    .errorOut(jsonBody[ErrorsOutputDto].description("Array of errors if there are any").example(exampleErrors))
    .out(jsonBody[ConnectedOutputDto].description("JSON result").example(exampleConnectedTrueOutputDto).example(exampleConnectedFalseOutputDto))
    .description(
      "Returns whether two “developers” are fully connected or no"
    )

}