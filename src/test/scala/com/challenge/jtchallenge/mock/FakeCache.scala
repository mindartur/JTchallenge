/*
 * Copyright (c) 2021 Artur
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.challenge.jtchallenge.mock

import cats.effect.{IO, Sync}
import cats.implicits._
import scalacache.{AbstractCache, CacheConfig}
import scalacache.logging.Logger

import scala.collection.mutable
import scala.concurrent.duration.Duration

final class FakeCache[V](implicit sync: Sync[IO]) extends AbstractCache[IO, V]{
  val underlying: mutable.Map[String, V] = mutable.Map[String, V]()

  override protected def doGet(key: String): IO[Option[V]] = IO(underlying.get(key))

  override protected def doPut(key: String, value: V, ttl: Option[Duration]): IO[Unit] = IO(underlying(key) = value)

  override protected def doRemove(key: String): IO[Unit] = IO(underlying.remove(key))

  override protected def doRemoveAll: IO[Unit] = IO(underlying.clear())

  override def config: CacheConfig = CacheConfig.defaultCacheConfig

  override def close: IO[Unit] = IO.unit

  override protected def logger: Logger[IO] = Logger.getLogger[IO](getClass.getName)(sync)

  override protected implicit def F: Sync[IO] = sync
}
