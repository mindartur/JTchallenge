/*
 * Copyright (c) 2021 Artur
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.challenge.jtchallenge.dto

import com.challenge.jtchallenge.models.OrganisationName
import cats.syntax.functor._
import io.circe.{Decoder, Encoder, Json}
import io.circe.generic.auto._
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.syntax._
import io.circe.refined._

sealed trait ConnectedOutputDto {
  val connected: Boolean
}

case class ConnectedFalseOutputDto()  extends ConnectedOutputDto {
  override val connected = false
}

case class ConnectedTrueOutputDto (organisations: List[OrganisationName]) extends ConnectedOutputDto {
  override val connected = true
}

object ConnectedOutputDto {
  implicit val encoder: Encoder[ConnectedOutputDto] = Encoder.instance {
    case foo @ ConnectedFalseOutputDto() => foo.asJson
    case bar @ ConnectedTrueOutputDto(_) => bar.asJson
  }

  implicit val decoder: Decoder[ConnectedOutputDto] =
    List[Decoder[ConnectedOutputDto]](
      Decoder[ConnectedFalseOutputDto].widen,
      Decoder[ConnectedTrueOutputDto].widen,
    ).reduceLeft(_ or _)

}

object ConnectedFalseOutputDto {
  implicit val decoder: Decoder[ConnectedFalseOutputDto] = deriveDecoder[ConnectedFalseOutputDto]
//  implicit val encoder: Encoder[ConnectedFalseOutputDto] = deriveEncoder[ConnectedFalseOutputDto]
  implicit val encoder: Encoder[ConnectedFalseOutputDto] = new Encoder[ConnectedFalseOutputDto] {
    final def apply(a: ConnectedFalseOutputDto): Json = Json.obj(
      ("connected", Json.fromBoolean(false)),
    )
  }
}

object ConnectedTrueOutputDto {
  implicit val decoder: Decoder[ConnectedTrueOutputDto] = deriveDecoder[ConnectedTrueOutputDto]
//  implicit val encoder: Encoder[ConnectedTrueOutputDto] = deriveEncoder[ConnectedTrueOutputDto]
  implicit val encoder: Encoder[ConnectedTrueOutputDto] = new Encoder[ConnectedTrueOutputDto] {
    final def apply(a: ConnectedTrueOutputDto): Json = Json.obj(
      ("connected", Json.fromBoolean(true)),
      ("organisations", Json.fromValues(a.organisations.map(x => Json.fromString(x.value))))
    )
  }
}
