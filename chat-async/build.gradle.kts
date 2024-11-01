import org.jetbrains.kotlin.js.translate.context.Namer.kotlin

plugins {
    kotlin("jvm") version "1.9.23"
}

group = "com.picbel"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}