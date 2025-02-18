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
    implementation("com.github.ajalt.clikt:clikt:5.0.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.3")
}

kotlin {
    jvmToolchain(17)
}

ktlint {
    version = "0.50.0"
}

tasks.test {
    useJUnitPlatform()
}

tasks {
    val fatJar = register<Jar>("fatJar") {
        dependsOn.addAll(listOf("compileJava", "compileKotlin", "processResources"))
        archiveClassifier.set("standalone")
        destinationDirectory = rootDir.resolve("artifacts/")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        manifest { attributes(mapOf("Main-Class" to application.mainClass)) }
        val sourcesMain = sourceSets.main.get()
        val contents = configurations.runtimeClasspath.get().map {
            if (it.isDirectory) it else zipTree(it)
        } + sourcesMain.output
        from(contents)
    }
    build {
        dependsOn(fatJar)
    }
}

application {
    mainClass.set("turingmachine.MainKt")
}
