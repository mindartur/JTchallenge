/*
 * Copyright (c) 2021 Artur
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.challenge.jtchallenge.mock

import cats.data.{ EitherNel, NonEmptyList }
import cats.effect.IO
import com.challenge.jtchallenge.models.UserName
import com.challenge.jtchallenge.services.TwitterService
import com.danielasfregola.twitter4s.entities.{
  Relationship,
  RelationshipOverview,
  RelationshipSource,
  RelationshipTarget
}

object TwitterServiceMock {
  def withErrors(errors: List[String]): TwitterService = { (_: UserName, _: UserName) =>
    IO.pure(Left(NonEmptyList.fromList(errors).get))
  }

  def withResult(following: Boolean, followedBy: Boolean): TwitterService = { (_: UserName, _: UserName) =>
    IO.pure(
      Right(
        Relationship(
          RelationshipOverview(
            RelationshipSource(
              id = 123,
              id_str = "123",
              screen_name = "dev1",
              following = following,
              followed_by = followedBy
            ),
            RelationshipTarget(
              id = 234,
              id_str = "234",
              screen_name = "dev2",
              following = followedBy,
              followed_by = following
            )
          )
        )
      )
    )
  }

  def withInternalError(error: String): TwitterService = { (_: UserName, _: UserName) =>
    IO.raiseError(new Exception(error))
  }
}
