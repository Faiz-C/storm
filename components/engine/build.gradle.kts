plugins {
  id("java")
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
  implementation(project(":components:core"))
  implementation(project(":components:physics"))
  implementation(project(":components:sound"))

  annotationProcessor("org.projectlombok:lombok:1.18.24")
  testAnnotationProcessor("org.projectlombok:lombok:1.18.24")
}

javafx {
  version = "18.0.1"
  modules = listOf("javafx.controls", "javafx.graphics")
}