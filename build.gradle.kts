import io.micronaut.gradle.MicronautRuntime
import io.micronaut.gradle.MicronautTestRuntime
import io.micronaut.gradle.openapi.DefaultOpenApiExtension
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.25"
    id("org.jetbrains.kotlin.kapt") version "1.9.25"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.9.25"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.micronaut.application") version "4.4.3-SNAPSHOT"
    id("io.micronaut.openapi") version "4.4.3-SNAPSHOT"
}

version = "0.1"
group = "com.example"

val kotlinVersion=project.properties.get("kotlinVersion")
repositories {
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/") {
        mavenContent { snapshotsOnly() }
    }
    mavenCentral()
}

dependencies {
    kapt("io.micronaut:micronaut-http-validation")
    kapt("io.micronaut.openapi:micronaut-openapi")
    kapt("io.micronaut.serde:micronaut-serde-processor")
    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
    implementation("io.micronaut.serde:micronaut-serde-jackson")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${kotlinVersion}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}")
    compileOnly("io.micronaut:micronaut-http-client")
    compileOnly("io.micronaut.openapi:micronaut-openapi-annotations")
    runtimeOnly("ch.qos.logback:logback-classic")
    runtimeOnly("com.fasterxml.jackson.module:jackson-module-kotlin")
    testImplementation("io.micronaut:micronaut-http-client")
}


application {
    mainClass = "com.example.ApplicationKt"
}
java {
    sourceCompatibility = JavaVersion.toVersion("17")
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

graalvmNative.toolchainDetection = false

micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("com.example.*")
    }

    openapi {
        // Fixes kotlin codegen with auth
        version.set("6.12.0")
        client(File("openapi.json")) {
            useOneOfInterfaces.set(false)
            clientId.set("example-api")
            // There is a bug in openapi Kotlin gen, not just micronaut's kotlin gen
            lang.set("kotlin")
            // We are not using auth for this project, instead headers are passed in as function params to allow easier
            // customization during tests. Otherwise, you have to constantly rebuild the context for the Client bean to
            // pull in new api path versions, tokens, etc.
            useAuth.set(false)
            useReactive.set(false)
        }
    }
}

tasks {

    register("helloWorld") {
        doLast {
            println("Hello, World!")
        }
    }

    // Doesn't work when trying to get the task by name
    named("openApiGenerateClient") {
        dependsOn("helloWorld")
    }

    // Works to reference it in depends on
    register("DepndsOnOpenApiGenerateClient") {
        dependsOn("openApiGenerateClient")
        doLast {
            println("Dependency Worked!")
        }
    }
}



