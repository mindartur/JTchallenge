/*
 * Copyright (c) 2021 Artur
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.challenge.jtchallenge.services

import cats.data.{EitherNel, EitherT, NonEmptyList}
import cats.effect.IO
import com.danielasfregola.twitter4s.TwitterRestClient
import com.challenge.jtchallenge.models.UserName
import com.danielasfregola.twitter4s.entities.Relationship
import com.danielasfregola.twitter4s.exceptions.{Errors, TwitterError, TwitterException}
import scalacache.Cache
import scalacache.memoization.memoizeF
import scala.concurrent.duration._


trait TwitterService {
  def relationshipBetweenUsers(userNameFollowing: UserName, userName: UserName): IO[EitherNel[String, Relationship]]
}

final class TwitterServiceImpl()(implicit cache: Cache[IO, EitherNel[String, Relationship]]) extends TwitterService {
  val restClient: TwitterRestClient = TwitterRestClient()

  override def relationshipBetweenUsers(
      userNameFollowing: UserName,
      userName: UserName
  ): IO[EitherNel[String, Relationship]] = memoizeF(Some(1.hour)) {
    IO
      .fromFuture(
        IO(
          restClient.relationshipBetweenUsers(userNameFollowing.value, userName.value)
        )
      )
      .map(x => Right(x.data))
      .handleErrorWith { case TwitterException(_, errors) =>
        errors.errors match {
          case Seq() => throw new Exception("TwitterService failed but doesn't have any error information")
          case head :: tail =>
            IO.pure(
              Left(
                NonEmptyList(head, tail)
                  .map((exception: TwitterError) => s"Twitter API returned error: ${exception.toString()}")
              )
            )
        }
      }
  }
}
