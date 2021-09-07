/*
 * Copyright (c) 2021 Artur
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.challenge.jtchallenge.dto

import com.challenge.jtchallenge.models.OrganisationName
import io.circe.{ Decoder, Encoder, HCursor }
import io.circe.generic.semiauto.{ deriveDecoder, deriveEncoder }
import io.circe.refined._

final case class GithubOrganisationDto(
    login: OrganisationName,
    id: Int
)
object GithubOrganisationDto {
  implicit val decoder: Decoder[GithubOrganisationDto] = deriveDecoder[GithubOrganisationDto]
  implicit val encoder: Encoder[GithubOrganisationDto] = deriveEncoder[GithubOrganisationDto]

}
