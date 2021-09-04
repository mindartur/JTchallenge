/*
 * Copyright (c) 2021 Artur
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.challenge.jtchallenge

import eu.timepit.refined._
import eu.timepit.refined.api._
import eu.timepit.refined.auto._
import eu.timepit.refined.cats._
import eu.timepit.refined.collection._
import eu.timepit.refined.string._

package object db {
  type JDBCDriverName =
    String Refined MatchesRegex[W.`"^\\\\w+\\\\.[\\\\w\\\\d\\\\.]+[\\\\w\\\\d]+$"`.T]
  object JDBCDriverName extends RefinedTypeOps[JDBCDriverName, String] with CatsRefinedTypeOpsSyntax

  type JDBCUrl = String Refined MatchesRegex[W.`"^jdbc:[a-zA-z0-9]+:.*"`.T]
  object JDBCUrl extends RefinedTypeOps[JDBCUrl, String] with CatsRefinedTypeOpsSyntax

  type JDBCUsername = String Refined NonEmpty
  object JDBCUsername extends RefinedTypeOps[JDBCUsername, String] with CatsRefinedTypeOpsSyntax

  type JDBCPassword = String Refined NonEmpty
  object JDBCPassword extends RefinedTypeOps[JDBCPassword, String] with CatsRefinedTypeOpsSyntax
}
