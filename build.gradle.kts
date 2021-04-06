import org.jetbrains.compose.compose

plugins {
    kotlin("jvm") version "1.4.31"
    id("org.jetbrains.compose") version "0.3.2"
    id("com.github.johnrengelman.shadow") version "6.1.0"
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

tasks {
    compileKotlin {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "15"
    }   }

    jar {
        manifest {
            attributes(mapOf("Main-Class" to "com.example.zucker.easycrypto.MainKt"))
}   }   }

compose.desktop {
    application {
        mainClass = "com.example.zucker.easycrypto.MainKt"
}   }

tasks.register("jlink") {
    dependsOn(tasks.shadowJar)
    group = "build"

    val outputDir = file("$buildDir/jlink/EasyCrypto")
    outputDir.deleteRecursively()

    exec {
        executable = "jlink"
        this.args = listOf(
            "--compress", "2",
            "--output", "$outputDir",
            "--add-modules", "java.desktop")
    }

    doLast {
        mapOf(
            tasks.shadowJar.get().archiveFile to "$outputDir/lib",
            "resources/easy-crypto.bat" to "$outputDir/bin",
            "resources/start-app.bat" to outputDir
        ).forEach { (source, dest) ->

            copy {
                from(source)
                into(dest)
    }   }   }
}
