/*
 * Copyright (c) 2021 Artur
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.challenge.jtchallenge.services

import cats.data.{EitherNel, EitherT}
import cats.effect.IO
import cats.implicits._
import com.challenge.jtchallenge.dto.{ConnectedFalseOutputDto, ConnectedOutputDto, ConnectedTrueOutputDto, GithubOrganisationDto}
import com.challenge.jtchallenge.models.{Developer, Organisation}


trait ConnectionsService {
  def areConnected(dev1: Developer, dev2: Developer): IO[EitherNel[String, ConnectedOutputDto]]
}

final class ConnectionsServiceImpl(
    val twitterService: TwitterService,
    val gitHubService: GitHubService
) extends ConnectionsService {

  def areConnected(dev1: Developer, dev2: Developer): IO[EitherNel[String, ConnectedOutputDto]] = {
    val areFollowingEachOtherE = EitherT(areFollowingEachOther(dev1, dev2))
    val commonOrganisationsE   = EitherT(getCommonOrganisations(dev1, dev2))
    (areFollowingEachOtherE, commonOrganisationsE).parMapN { (areFollowingEachOther, commonOrganisations) =>
      getConnectedOutput(areFollowingEachOther, commonOrganisations)
    }(EitherT.accumulatingParallel).value
  }

  def areFollowingEachOther(dev1: Developer, dev2: Developer): IO[EitherNel[String, Boolean]] = {
    EitherT(twitterService.relationshipBetweenUsers(dev1.userName, dev2.userName))
      .map(x => x.relationship.source.following && x.relationship.target.following)
      .value
  }

  def getCommonOrganisations(dev1: Developer, dev2: Developer): IO[EitherNel[String, List[Organisation]]] = {
    val toOrganisation = (gOrg: GithubOrganisationDto) => Organisation(gOrg.login)

    val user1Orgs = EitherT(gitHubService.getUserOrganisations(dev1.userName)).map(orgs => orgs.map(toOrganisation))
    val user2Orgs = EitherT(gitHubService.getUserOrganisations(dev2.userName)).map(orgs => orgs.map(toOrganisation))
    (user1Orgs, user2Orgs).parMapN { (user1Orgs, user2Orgs) =>
      intersectOrganisations(user1Orgs, user2Orgs)
    }.value
  }

  private def getConnectedOutput(
      areFollowingEachOther: Boolean,
      commonOrganisations: List[Organisation]
  ): ConnectedOutputDto =
    if (areFollowingEachOther && commonOrganisations.nonEmpty)
      ConnectedTrueOutputDto(commonOrganisations.map(_.name))
    else
      ConnectedFalseOutputDto()

  private def intersectOrganisations(orgs1: List[Organisation], orgs2: List[Organisation]): List[Organisation] =
    if (orgs1.length >= orgs2.length)
      orgs1.intersect(orgs2)
    else
      orgs2.intersect(orgs1)
}
