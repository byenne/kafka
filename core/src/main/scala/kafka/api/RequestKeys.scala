/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kafka.api

object RequestKeys {
  val Produce: Short = 0
  val Fetch: Short = 1
  val MultiFetch: Short = 2
  val MultiProduce: Short = 3
  val Offsets: Short = 4
  val AckedProduce : Short = 5
  val AckedMultiProduce : Short = 6

  def name(id: Int): String = id match {
    case Produce => "produce"
    case Fetch => "fetch"
    case MultiFetch => "multi-fetch"
    case MultiProduce => "multi-produce"
    case Offsets => "offset"
    case AckedProduce => "acked-produce"
    case AckedMultiProduce => "acked-multi-produce"
    case _ => "unknown-" + id
  }
}
