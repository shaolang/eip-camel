plugins {
    id("org.jetbrains.kotlin.jvm") version "1.3.72"
    application
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.apache.logging.log4j:log4j-slf4j18-impl:2.13.3")

    for (n in listOf("core-engine", "core-languages", "bean", "direct",
                     "stream", "activemq")) {
        implementation("org.apache.camel:camel-$n:3.3.0")
    }

    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
    testImplementation("org.apache.camel:camel-test:3.3.0")
}

application {
    println("Select the sample to run:")
    val samples = listOf(listOf("Point-to-Point Channel", "eip.Point2PointKt"))

    for (item in samples.withIndex()) {
        println("${item.index + 1}: ${item.value[0]}")
    }

    val selected = readLine()?.trim()?.toIntOrNull()
    if (selected is Int && selected <= samples.size && selected > 0) {
        println("Running ${selected}:  ${samples[selected - 1][0]}")
        mainClassName = samples[selected - 1][1]
    } else {
        println("Invalid selection: ${selected}")
    }
}
