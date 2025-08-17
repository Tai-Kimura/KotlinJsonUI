plugins {
    kotlin("jvm") version "1.9.21"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.squareup:kotlinpoet:1.15.3")
    implementation("com.github.ajalt.clikt:clikt:4.2.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
    implementation("ch.qos.logback:logback-classic:1.4.11")
    
    testImplementation(kotlin("test"))
}

application {
    mainClass.set("com.kotlinjsonui.tools.cli.MainKt")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}