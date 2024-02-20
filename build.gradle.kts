import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version("1.9.22")
  id("org.openjfx.javafxplugin") version ("0.1.0")
}

javafx {
  version = "20.0.2"
  modules("javafx.controls", "javafx.graphics", "javafx.media")
}

tasks.wrapper {
  gradleVersion = "8.6"
  distributionType = Wrapper.DistributionType.ALL
}

allprojects {
  apply(plugin = "java")
  apply(plugin = "java-library")
  apply(plugin = "maven-publish")
  apply(plugin = "org.openjfx.javafxplugin")
  apply(plugin = "org.jetbrains.kotlin.jvm")

  group = "org.storm"
  layout.buildDirectory.set(File("${rootProject.projectDir}${File.separator}build", project.name))

  repositories {
    mavenCentral()
  }

  java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
  }

  dependencies {
    // Versions
    val slf4jVersion = "2.0.3"
    val jacksonVersion = "2.15.3"

    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-javafx:1.6.4")

    implementation("org.slf4j:slf4j-api:$slf4jVersion")
    implementation("org.slf4j:slf4j-simple:$slf4jVersion")

    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")

    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")
  }

  tasks {
    withType<KotlinCompile> {
      kotlinOptions.jvmTarget = "21"
    }
  }
}
