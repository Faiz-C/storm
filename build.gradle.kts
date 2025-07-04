plugins {
    kotlin("jvm") version("2.1.0")
    id("org.openjfx.javafxplugin") version ("0.1.0")
}

javafx {
    version = "21.0.2"
    modules("javafx.controls", "javafx.graphics", "javafx.media")
}

tasks.wrapper {
    gradleVersion = "8.9"
    distributionType = Wrapper.DistributionType.ALL
}

allprojects {
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")
    apply(plugin = "org.openjfx.javafxplugin")
    apply(plugin = "org.jetbrains.kotlin.jvm")

    group = "org.storm"
    layout.buildDirectory.set(File("${rootProject.projectDir}${File.separator}build", project.name))

    repositories {
        mavenCentral()
    }

    dependencies {
        // Versions
        val slf4jVersion = "2.0.16"
        val jacksonVersion = "2.18.1"
        val coroutinesVersion = "1.9.0"
        val junitVersion = "5.9.0"

        api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
        api("org.jetbrains.kotlinx:kotlinx-coroutines-javafx:$coroutinesVersion")

        api("org.slf4j:slf4j-api:$slf4jVersion")
        api("org.slf4j:slf4j-simple:$slf4jVersion")

        api("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
        api("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")
        api("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:$jacksonVersion")
        api("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")

        api("org.apache.commons:commons-lang3:3.17.0")
        api("com.github.ben-manes.caffeine:caffeine:3.1.8")

        testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
        testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    }

    kotlin {
        jvmToolchain(21)
    }

    tasks {
        test {
            useJUnitPlatform()
        }
    }
}
