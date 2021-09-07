/*
 * Copyright (c) 2021 Artur
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.challenge.jtchallenge.config

import eu.timepit.refined.auto._
import eu.timepit.refined.pureconfig._
import pureconfig._
import pureconfig.generic.semiauto._

/**
  * Github service configuration.
  *
  * @param accessToken   Github access token
  */
final case class GithubConfig(accessToken: GithubToken)

object GithubConfig {
  // The default configuration key to lookup the service configuration.
  final val CONFIG_KEY: ConfigKey = "github"

  implicit val serviceConfigReader: ConfigReader[GithubConfig] = deriveReader[GithubConfig]

}
