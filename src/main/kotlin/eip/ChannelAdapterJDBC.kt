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

import javax.sql.DataSource
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.impl.DefaultCamelContext
import org.sqlite.SQLiteDataSource


fun main() {
    val datasource = setupDatabase()
    val context = DefaultCamelContext()

    context.registry.bind("datasource", datasource)

    context.addRoutes(object: RouteBuilder() {
        override fun configure() {
            from("direct:trigger")
                .to("jdbc:datasource")
        }
    })

    context.start()

    val producer = context.createProducerTemplate()

    producer.sendBody("direct:trigger", "INSERT INTO foo VALUES('hi', 'world')")
    readOneRowFromFoo(datasource)

    context.stop()
}


private fun setupDatabase(): DataSource {
    val ds = SQLiteDataSource()
    ds.setUrl("jdbc:sqlite:./build/channel-adapter.db")

    ds.getConnection().use { conn ->
        val stmt = conn.createStatement()
        stmt.execute("DROP TABLE IF EXISTS foo")
        stmt.execute("CREATE TABLE foo (bar, baz)")
    }

    return ds
}


private fun readOneRowFromFoo(datasource: DataSource) {
    datasource.getConnection().use { conn ->
        val stmt = conn.createStatement()
        val resultSet = stmt.executeQuery("SELECT bar, baz FROM foo")
        resultSet.next()

        println("bar=${resultSet.getString(1)}, baz=${resultSet.getString(2)}")
    }
}
