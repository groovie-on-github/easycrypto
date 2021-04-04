import org.jetbrains.compose.compose

plugins {
    kotlin("jvm") version "1.4.31"
    id("org.jetbrains.compose") version "0.3.2"
}

group = "zucker.easycrypto"
version = "0.1.0"

repositories {
    jcenter()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation( "org.bouncycastle:bcprov-jdk15on:1.68")
}

tasks.compileKotlin {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "15"
}   }

compose.desktop {
    application {
        mainClass = "com.example.zucker.easycrypto.MainKt"
}   }