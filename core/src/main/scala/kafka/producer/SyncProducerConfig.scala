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

package kafka.producer

import kafka.utils.Utils
import java.util.Properties
import kafka.message.{CompressionUtils, CompressionCodec}

class SyncProducerConfig(val props: Properties) extends SyncProducerConfigShared {
  /** the broker to which the producer sends events */
  val host = Utils.getString(props, "host")

  /** the port on which the broker is running */
  val port = Utils.getInt(props, "port")
}

trait SyncProducerConfigShared {
  val props: Properties
  
  val bufferSize = Utils.getInt(props, "buffer.size", 100*1024)

  val connectTimeoutMs = Utils.getInt(props, "connect.timeout.ms", 5000)

  /** the socket timeout for network requests */
  val socketTimeoutMs = Utils.getInt(props, "socket.timeout.ms", 30000)  

  val reconnectInterval = Utils.getInt(props, "reconnect.interval", 30000)

  val maxMessageSize = Utils.getInt(props, "max.message.size", 1000000)

  /** whether we wait or not to ACK publish requests */
  val ackNeeded = Utils.getBoolean(props, "ack.needed", false)

}
