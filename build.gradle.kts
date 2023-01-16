import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version("1.8.0")
  id("org.openjfx.javafxplugin") version ("0.0.13")
}

javafx {
  version = "18.0.1"
  modules("javafx.controls", "javafx.graphics", "javafx.media")
}

tasks.wrapper {
  gradleVersion = "7.6"
  distributionType = Wrapper.DistributionType.ALL
}

allprojects {
  apply(plugin = "java")
  apply(plugin = "java-library")
  apply(plugin = "maven-publish")
  apply(plugin = "org.openjfx.javafxplugin")
  apply(plugin = "org.jetbrains.kotlin.jvm")

  group = "org.storm"
  buildDir = File("${rootProject.projectDir}${File.separator}build", project.name)

  repositories {
    mavenCentral()
  }

  java {
    sourceCompatibility = JavaVersion.VERSION_18
    targetCompatibility = JavaVersion.VERSION_18
  }

  dependencies {
    // Versions
    val slf4jVersion = "2.0.3"

    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-javafx:1.6.4")

    implementation("org.slf4j:slf4j-api:$slf4jVersion")
    implementation("org.slf4j:slf4j-simple:$slf4jVersion")
  }

  tasks {
    withType<KotlinCompile> {
      kotlinOptions.jvmTarget = "18"
    }
  }
}
