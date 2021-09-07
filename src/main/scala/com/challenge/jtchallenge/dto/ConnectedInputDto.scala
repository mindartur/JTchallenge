/*
 * Copyright (c) 2021 Artur
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.challenge.jtchallenge.dto

import com.challenge.jtchallenge.models.UserName
import sttp.tapir.EndpointIO.annotations.path

case class ConnectedInputDto(
    @path
    dev1: UserName,
    @path
    dev2: UserName
)
