/*
 * Copyright (c) 2021 Artur
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.challenge.jtchallenge.services

import cats.data.{EitherNel, NonEmptyList}
import cats.effect.IO
import com.challenge.jtchallenge.config.GithubConfig
import com.challenge.jtchallenge.dto.GithubOrganisationDto
import com.challenge.jtchallenge.models.UserName
import cats.implicits._
import github4s.http.HttpClient
import github4s.interpreters.StaticAccessToken
import github4s.{GHResponse, GithubConfig => GConfig}
import org.http4s.client.Client

trait GitHubService {
  def getUserOrganisations(userName: UserName): IO[EitherNel[String, List[GithubOrganisationDto]]]
}

final class GitHubServiceImpl(githubConfig: GithubConfig)(implicit httpClient: Client[IO]) extends GitHubService {
  val client = new HttpClient[IO](httpClient, GConfig.default, new StaticAccessToken(Some(githubConfig.accessToken.value)))

  override def getUserOrganisations(username: UserName): IO[EitherNel[String, List[GithubOrganisationDto]]] = {
     client
       .get[List[GithubOrganisationDto]](s"users/$username/orgs")
       .map((response: GHResponse[List[GithubOrganisationDto]]) =>
         response.result.bimap(
           error => NonEmptyList.of(error.getMessage()),
           identity
         )
       )
  }
}

