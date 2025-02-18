plugins {
    kotlin("jvm") version "1.9.24"
    application
    id("org.jlleitschuh.gradle.ktlint") version "11.6.0"
}

group = "ru.itmo.ct.kotlin"
version = "1.0.0"

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.24")
    testImplementation(kotlin("test"))
    testImplementation("com.github.tschuchortdev:kotlin-compile-testing:1.6.0")
}

application {
    mainClass.set("MainKt")
}

kotlin {
    jvmToolchain(21)
}

ktlint {
    version = "0.50.0"
}

tasks.test {
    useJUnitPlatform()
}
