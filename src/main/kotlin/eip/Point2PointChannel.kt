/*
 * Copyright 2020 Shaolang Ai
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package eip

import org.apache.camel.builder.RouteBuilder
import org.apache.camel.impl.DefaultCamelContext

fun main() {
    val context = DefaultCamelContext()
    val directURI = "direct:greet"

    context.addRoutes(object: RouteBuilder() {
        override fun configure() {
            val mqURI = "activemq:queue:p2p.sample"

            from(directURI).to(mqURI)
            from(mqURI).to("stream:out")
        }
    })

    context.start()

    val producer = context.createProducerTemplate()
    for (n in 1..10) {
        producer.sendBody(directURI, "Hello, World! for the $n-th time")
    }

    Thread.sleep(100)
    context.stop()
}
