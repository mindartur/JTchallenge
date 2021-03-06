/*
 * Copyright (c) 2021 Artur
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.challenge.jtchallenge.mock

import cats.effect.{ IO, Resource }
import org.http4s.{ Response, Status }
import org.http4s.client.Client
import fs2.Stream
import io.circe.{ Encoder }

object HttpClientMock {
  def withStatusAndBody(status: Status, body: String = ""): Client[IO] =
    Client.apply[IO] { _ =>
      Resource.eval(
        IO(
          Response[IO](
            status = status,
            body = Stream.emits(body.getBytes("UTF-8"))
          )
        )
      )
    }

  def successWith[A: Encoder](result: A): Client[IO] =
    Client.apply[IO] { _ =>
      Resource.eval(
        IO(
          Response[IO](
            status = Status.Ok,
            body = Stream.emits(Encoder[A](implicitly)(result).toString().getBytes("UTF-8"))
          )
        )
      )
    }

  def withInternalError(error: String): Client[IO] =
    Client.apply[IO] { _ =>
      Resource.eval(IO.raiseError(new Exception(error)))
    }
}
