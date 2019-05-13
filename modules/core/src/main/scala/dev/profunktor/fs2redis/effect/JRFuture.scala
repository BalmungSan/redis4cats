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

package dev.profunktor.redis4cats.effect

import java.util.concurrent.{ CompletableFuture, CompletionStage, Future }

import cats.effect.{ Async, ContextShift }
import cats.implicits._
import io.lettuce.core.{ ConnectionFuture, RedisFuture }

object JRFuture {

  case class EmptyValue(msg: String = "Empty value") extends Throwable(msg)

  private[redis4cats] type JFuture[A] = CompletionStage[A] with Future[A]

  def apply[F[_]: Async: ContextShift, A](fa: F[RedisFuture[A]]): F[A] =
    liftJFuture[F, RedisFuture[A], A](fa)

  def fromConnectionFuture[F[_]: Async: ContextShift, A](fa: F[ConnectionFuture[A]]): F[A] =
    liftJFuture[F, ConnectionFuture[A], A](fa)

  def fromCompletableFuture[F[_]: Async: ContextShift, A](fa: F[CompletableFuture[A]]): F[A] =
    liftJFuture[F, CompletableFuture[A], A](fa)

  private[redis4cats] def liftJFuture[F[_], G <: JFuture[A], A](
      fa: F[G]
  )(implicit F: Async[F], cs: ContextShift[F]): F[A] =
    fa.flatMap { f =>
      F.async[A] { cb =>
        f.handle[Unit] { (value: A, t: Throwable) =>
          if (t != null) cb(Left(t))
          else cb(Right(value))
        }
        ()
      } <* cs.shift
    }

}
