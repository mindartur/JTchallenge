/*
 * Copyright (c) 2021 Artur
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.challenge.jtchallenge

import eu.timepit.refined.api.{ Refined, RefinedTypeOps }
import eu.timepit.refined.cats.{ CatsRefinedTypeOpsSyntax }
import eu.timepit.refined.collection.NonEmpty

package object dto {
  type ServiceError = String Refined NonEmpty
  object ServiceError extends RefinedTypeOps[ServiceError, String] with CatsRefinedTypeOpsSyntax

  type ServiceErrors[A] = List[A] Refined NonEmpty

}
