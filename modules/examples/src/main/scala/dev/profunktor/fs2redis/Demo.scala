/*
 * Copyright 2018-2019 ProfunKtor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.profunktor.fs2redis

import cats.effect.IO
import dev.profunktor.fs2redis.codecs.Codecs
import dev.profunktor.fs2redis.codecs.splits._
import dev.profunktor.fs2redis.domain.{ DefaultRedisCodec, Fs2RedisCodec }
import io.lettuce.core.codec.StringCodec

object Demo {

  val redisURI: String                           = "redis://localhost"
  val redisClusterURI: String                    = "redis://localhost:30001"
  val stringCodec: Fs2RedisCodec[String, String] = DefaultRedisCodec(StringCodec.UTF8)
  val longCodec: Fs2RedisCodec[String, Long]     = Codecs.derive[String, Long](stringCodec, stringLongEpi)

  def putStrLn[A](a: A): IO[Unit] = IO(println(a))

}
