plugins {
    kotlin("jvm") version "1.9.24"
    kotlin("plugin.serialization") version "1.9.24"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    application
}

group = "com.emotionfriend"
version = "1.0.0"

application {
    mainClass.set("com.emotionfriend.ApplicationKt")
}

repositories {
    mavenCentral()
}

val ktorVersion = "2.3.12"

dependencies {
    // Ktor server
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging:$ktorVersion")
    implementation("io.ktor:ktor-server-default-headers:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.4.14")

    // Database connection pool + MySQL driver
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("com.mysql:mysql-connector-j:8.3.0")

    // Test
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.9.24")
}

tasks.shadowJar {
    archiveBaseName.set("emotion-friend-backend")
    archiveClassifier.set("all")
    archiveVersion.set("")
    mergeServiceFiles()
}
