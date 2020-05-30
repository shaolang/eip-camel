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

class TopicSubscriber(val name: String) {
    fun namedEcho(s: String): String {
        return "$name received: $s"
    }
}

fun main() {
    val context = DefaultCamelContext()
    val foo = TopicSubscriber("foo")
    val bar = TopicSubscriber("bar")
    val directBazURI = "direct:wc.baz"
    val directQuzURI = "direct:wc.quz"

    context.addRoutes(object: RouteBuilder() {
        override fun configure() {
            val bazURI = "activemq:topic:wc.baz"
            val quzURI = "activemq:topic:wc.quz"

            from(directBazURI).to(bazURI)
            from(directQuzURI).to(quzURI)

            from(bazURI).bean(foo, "namedEcho").to("stream:out")
            from("activemq:topic:wc.*").bean(bar, "namedEcho").to("stream:out")
        }
    })

    context.start()

    val producer = context.createProducerTemplate()
    producer.sendBody(directBazURI, "Hello, World!")
    producer.sendBody(directQuzURI, "Goodbye, Universe!")

    Thread.sleep(100)
    context.stop()
}
