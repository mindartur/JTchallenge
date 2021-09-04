package com.challenge.jtchallenge.dto

import com.challenge.jtchallenge.models.OrganisationName
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.refined._

final case class GithubOrganisationDto (
  id: OrganisationName
)
object GithubOrganisationDto {
  implicit val decoder: Decoder[GithubOrganisationDto] = deriveDecoder[GithubOrganisationDto]
  implicit val encoder: Encoder[GithubOrganisationDto] = deriveEncoder[GithubOrganisationDto]

}
