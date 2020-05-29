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

    context.stop()
}
