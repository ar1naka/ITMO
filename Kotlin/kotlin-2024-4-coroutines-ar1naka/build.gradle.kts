val kotlinVersion = "1.9.20"
val ktlintVersion = "11.6.0"

plugins {
    kotlin("jvm") version "1.9.24"
    id("org.jlleitschuh.gradle.ktlint") version "11.6.0"
}

allprojects {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }

//    dependencies {
//        implementation("com.github.ajalt.clikt:clikt:5.0.1")
//        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
//        testImplementation(kotlin("test"))
//        testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.3")
//    }
}

subprojects {
    apply {
        plugin("kotlin")
        plugin("org.jlleitschuh.gradle.ktlint")
    }

    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
        testImplementation(kotlin("test"))
    }

    ktlint {
        version = "0.50.0"
    }

    tasks.test {
        useJUnitPlatform()
    }

    sourceSets {
        named("main") {
            kotlin.srcDir("src")
            java.srcDir("src")
        }
        named("test") {
            kotlin.srcDir("test")
            java.srcDir("test")
        }
    }
}

configure(subprojects.filterNot { it.name == "util" }) {
    dependencies {
        implementation(project(":util"))
        testImplementation(project(":util"))
    }
}
