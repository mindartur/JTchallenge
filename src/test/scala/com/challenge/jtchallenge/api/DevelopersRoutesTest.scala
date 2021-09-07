/*
 * Copyright (c) 2021 Artur
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.challenge.jtchallenge.api

import cats.effect.IO
import com.challenge.jtchallenge.dto.{
  ConnectedFalseOutputDto,
  ConnectedOutputDto,
  ConnectedTrueOutputDto,
  ErrorsOutputDto,
  GithubOrganisationDto
}
import com.challenge.jtchallenge.mock.{ ConnectionsServiceMock, HttpClientMock, TwitterServiceMock }
import com.challenge.jtchallenge.services.ConnectionsService
import org.http4s.{ HttpRoutes, Method, Request, Status, Uri }
import org.http4s.server.Router
import munit._
import org.http4s.implicits._
import io.circe.generic.auto._
import eu.timepit.refined.auto._
import io.circe.Decoder
import io.circe.parser.decode

class DevelopersRoutesTest extends CatsEffectSuite {
  test("when one dev parameter is missing") {
    val expectedStatusCode = Status.NotFound
    val connectionsService = ConnectionsServiceMock.get(
      TwitterServiceMock.withResult(following = true, followedBy = true),
      HttpClientMock.withStatusAndBody(Status.Ok, "")
    )

    Uri.fromString("/developers/connected/test//") match {
      case Left(_) =>
        fail("Could not generate valid URI!")
      case Right(u) =>
        def service: HttpRoutes[IO] = Router("/" -> new DevelopersRoutes(connectionsService).routes)
        val request = Request[IO](
          method = Method.GET,
          uri = u
        )
        val response = service.orNotFound.run(request)
        val test = for {
          result <- response
          body   <- result.as[String]
        } yield (result.status, body)
        test.assertEquals((expectedStatusCode, "Not found"))
    }
  }

  test("when dev is not following another dev on twitter but have common orgs") {
    testDevelopersRoutesResponse[ConnectedOutputDto, ConnectedFalseOutputDto](
      Status.Ok,
      ConnectedFalseOutputDto(),
      ConnectionsServiceMock.get(
        TwitterServiceMock.withResult(following = false, followedBy = true),
        HttpClientMock.successWith(List(GithubOrganisationDto("companyName", 123)))
      )
    )
  }

  test("when devs are following each other on twitter but don't have common orgs") {
    testDevelopersRoutesResponse[ConnectedOutputDto, ConnectedFalseOutputDto](
      Status.Ok,
      ConnectedFalseOutputDto(),
      ConnectionsServiceMock.get(
        TwitterServiceMock.withResult(following = true, followedBy = true),
        HttpClientMock.successWith(List[GithubOrganisationDto]())
      )
    )
  }

  test("when devs are following each other on twitter and have common orgs") {
    testDevelopersRoutesResponse[ConnectedOutputDto, ConnectedTrueOutputDto](
      Status.Ok,
      ConnectedTrueOutputDto(List("companyName")),
      ConnectionsServiceMock.get(
        TwitterServiceMock.withResult(following = true, followedBy = true),
        HttpClientMock.successWith(List(GithubOrganisationDto("companyName", 123)))
      )
    )
  }

  test("when github API returns errors") {
    testDevelopersRoutesResponse(
      Status.BadRequest,
      ErrorsOutputDto(List("Github API returned error: Bad Github identifier")),
      ConnectionsServiceMock.get(
        TwitterServiceMock.withResult(following = true, followedBy = true),
        HttpClientMock.withStatusAndBody(Status.BadRequest, """{"message": "Bad Github identifier"}""")
      )
    )
  }

  test("when twitter API returns errors") {
    testDevelopersRoutesResponse(
      Status.BadRequest,
      ErrorsOutputDto(
        List(
          "Bad Twitter identifier dev1",
          "Bad Twitter identifier dev2"
        )
      ),
      ConnectionsServiceMock.get(
        TwitterServiceMock.withErrors(List("Bad Twitter identifier dev1", "Bad Twitter identifier dev2")),
        HttpClientMock.successWith(List(GithubOrganisationDto("companyName", 123)))
      )
    )
  }

  test("when both github and twitter APIs return errors") {
    testDevelopersRoutesResponse(
      Status.BadRequest,
      ErrorsOutputDto(
        List(
          "Bad Twitter identifier dev1",
          "Bad Twitter identifier dev2",
          "Github API returned error: Bad Github identifier dev1"
        )
      ),
      ConnectionsServiceMock.get(
        TwitterServiceMock.withErrors(List("Bad Twitter identifier dev1", "Bad Twitter identifier dev2")),
        HttpClientMock.withStatusAndBody(Status.BadRequest, """{"message": "Bad Github identifier dev1"}""")
      )
    )
  }

  test("when there is Twitter internal service error") {
    testDevelopersRoutesRawResponse(
      Status.InternalServerError,
      "Internal server error",
      ConnectionsServiceMock.get(
        TwitterServiceMock.withInternalError("Network error"),
        HttpClientMock.successWith(List[GithubOrganisationDto]())
      )
    )
  }

  test("when there is Github internal service error") {
    testDevelopersRoutesRawResponse(
      Status.InternalServerError,
      "Internal server error",
      ConnectionsServiceMock.get(
        TwitterServiceMock.withResult(following = true, followedBy = true),
        HttpClientMock.withInternalError("Network error")
      )
    )
  }

  def testDevelopersRoutesResponse[B: Decoder, A <: B](
      expectedStatusCode: Status,
      expectedResult: A,
      connectionsService: ConnectionsService
  ): IO[Unit] =
    Uri.fromString("/developers/connected/dev1/dev2/") match {
      case Left(_) =>
        fail("Could not generate valid URI!")
      case Right(u) =>
        def service: HttpRoutes[IO] = Router("/" -> new DevelopersRoutes(connectionsService).routes)
        val request = Request[IO](
          method = Method.GET,
          uri = u
        )
        val response = service.orNotFound.run(request)
        val test = for {
          result   <- response
          body     <- result.as[String]
          jsonBody <- IO.fromEither(decode[B](body))
        } yield (result.status, jsonBody)
        test.assertEquals((expectedStatusCode, expectedResult))
    }

  def testDevelopersRoutesRawResponse(
      expectedStatusCode: Status,
      expectedResult: String,
      connectionsService: ConnectionsService
  ): IO[Unit] =
    Uri.fromString("/developers/connected/dev1/dev2/") match {
      case Left(_) =>
        fail("Could not generate valid URI!")
      case Right(u) =>
        def service: HttpRoutes[IO] = Router("/" -> new DevelopersRoutes(connectionsService).routes)
        val request = Request[IO](
          method = Method.GET,
          uri = u
        )
        val response = service.orNotFound.run(request)
        val test = for {
          result <- response
          body   <- result.as[String]
        } yield (result.status, body)
        test.assertEquals((expectedStatusCode, expectedResult))
    }
}
