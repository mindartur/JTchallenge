/*
 * Copyright (c) 2021 Artur
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.challenge.jtchallenge.mock

import cats.effect.IO
import com.challenge.jtchallenge.config.GithubConfig
import com.challenge.jtchallenge.services.{
  ConnectionsService,
  ConnectionsServiceImpl,
  GitHubServiceImpl,
  TwitterService
}
import org.http4s.client.Client
import eu.timepit.refined.auto._

object ConnectionsServiceMock {
  def get(twitterServiceMock: TwitterService, client: Client[IO]): ConnectionsService =
    new ConnectionsServiceImpl(
      twitterService = twitterServiceMock,
      gitHubService = new GitHubServiceImpl(GithubConfig("test"))(client)
    )
}
