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
import com.challenge.jtchallenge.models.{Organisation, UserName}

trait GitHubService {
  def getUserOrganisations(userName: UserName): IO[EitherNel[String, List[Organisation]]]
}

final class GitHubServiceImpl extends GitHubService {
  override def getUserOrganisations(userName: UserName): IO[EitherNel[String, List[Organisation]]] = IO(Left(NonEmptyList("something", List())))
}
