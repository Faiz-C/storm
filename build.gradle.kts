plugins {
  id("org.openjfx.javafxplugin") version ("0.0.13")
}

javafx {
  version = "18.0.1"
  modules("javafx.controls", "javafx.graphics", "javafx.media")
}

tasks.wrapper {
  gradleVersion = "7.5.1"
  distributionType = Wrapper.DistributionType.ALL
}

allprojects {
  apply(plugin = "java")
  apply(plugin = "java-library")
  apply(plugin = "maven-publish")
  apply(plugin = "org.openjfx.javafxplugin")

  group = "org.storm"

  repositories {
    mavenCentral()
  }

  java {
    sourceCompatibility = JavaVersion.VERSION_18
    targetCompatibility = JavaVersion.VERSION_18
  }

  dependencies {
    // Versions
    val lombokVersion = "1.18.24"
    val slf4jVersion = "2.0.3"

    implementation("org.projectlombok:lombok:$lombokVersion")
    implementation("org.slf4j:slf4j-api:$slf4jVersion")
    implementation("org.slf4j:slf4j-simple:$slf4jVersion")

    annotationProcessor("org.projectlombok:lombok:$lombokVersion")
    testAnnotationProcessor("org.projectlombok:lombok:$lombokVersion")
  }
}
