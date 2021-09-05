package com.challenge.jtchallenge.api

import cats.effect.IO
import com.challenge.jtchallenge.dto.{ConnectedFalseOutputDto, ConnectedOutputDto, ConnectedTrueOutputDto, GithubOrganisationDto}
import com.challenge.jtchallenge.mock.{ConnectionsServiceMock, HttpClientMock, TwitterServiceMock}
import com.challenge.jtchallenge.services.ConnectionsService
import org.http4s.{HttpRoutes, Method, Request, Status, Uri}
import org.http4s.server.Router
import munit._
import org.http4s.implicits._
import io.circe.generic.auto._
import eu.timepit.refined.auto._
import io.circe.parser.decode

class DevelopersRoutesTest  extends CatsEffectSuite {
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
    testDevelopersRoutesWithNoErrors(
      Status.Ok,
      ConnectedFalseOutputDto(),
      ConnectionsServiceMock.get(
        TwitterServiceMock.withResult(following = false, followedBy = true),
        HttpClientMock.successWith(List(GithubOrganisationDto("companyName", 123)))
      )
    )
  }

  test("when devs are following each other on twitter but don't have common orgs") {
    testDevelopersRoutesWithNoErrors(
      Status.Ok,
      ConnectedFalseOutputDto(),
      ConnectionsServiceMock.get(
        TwitterServiceMock.withResult(following = true, followedBy = true),
        HttpClientMock.successWith(List[GithubOrganisationDto]())
      )
    )
  }

  test("when devs are following each other on twitter and have common orgs") {
    testDevelopersRoutesWithNoErrors(
      Status.Ok,
      ConnectedTrueOutputDto(List("companyName")),
      ConnectionsServiceMock.get(
        TwitterServiceMock.withResult(following = true, followedBy = true),
        HttpClientMock.successWith(List(GithubOrganisationDto("companyName", 123)))
      )
    )
  }

  def testDevelopersRoutesWithNoErrors[A <: ConnectedOutputDto](
                               expectedStatusCode: Status,
                               expectedResult: A,
                               connectionsService: ConnectionsService
                          ): IO[Unit] = {

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
          jsonBody <- IO.fromEither(decode[ConnectedOutputDto](body))
        } yield (result.status, jsonBody)
        test.assertEquals((expectedStatusCode, expectedResult))
    }
  }
}
