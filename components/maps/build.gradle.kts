plugins {
  id("java-library")
  id("org.openjfx.javafxplugin") version ("0.0.13")
}

group = "org.storm"
version = "1.1.0"

repositories {
  mavenCentral()
}

val lombokVersion = "1.18.24"

dependencies {
  implementation(project(":components:core"))
  implementation(project(":components:physics"))
  annotationProcessor("org.projectlombok:lombok:${lombokVersion}")
}

javafx {
  version = "18.0.1"
  modules = listOf("javafx.controls", "javafx.graphics")
}