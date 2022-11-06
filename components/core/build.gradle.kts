plugins {
  id("java-library")
  id("org.openjfx.javafxplugin") version ("0.0.13")
}

group = "org.storm"
version = "1.1.0"

repositories {
  mavenCentral()
}

val slf4jVersion = "2.0.0-alpha2"
val lombokVersion = "1.18.24"

dependencies {
  api("org.projectlombok:lombok:$lombokVersion")
  api("org.slf4j:slf4j-api:$slf4jVersion")
  api("org.slf4j:slf4j-simple:$slf4jVersion")

  annotationProcessor("org.projectlombok:lombok:$lombokVersion")
}

javafx {
  version = "18.0.1"
  modules = listOf("javafx.controls", "javafx.graphics")
}