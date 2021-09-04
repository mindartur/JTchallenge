/*
 * Copyright (c) 2021 Artur
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.challenge.jtchallenge.dto

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.refined._

case class ErrorsOutputDto(errors: List[ServiceError])

object ErrorsOutputDto {
  implicit val decoder: Decoder[ErrorsOutputDto] = deriveDecoder[ErrorsOutputDto]
  implicit val encoder: Encoder[ErrorsOutputDto] = deriveEncoder[ErrorsOutputDto]

}
