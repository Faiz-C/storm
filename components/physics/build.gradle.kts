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
val junitVersion = "5.6.0"

dependencies {
  api(project(":components:core"))
  api("org.apache.commons:commons-math3:3.6.1")
  api("com.google.guava:guava:31.1-jre")

  testImplementation("org.mockito:mockito-core:4.8.0")
  testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
  testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitVersion")

  annotationProcessor("org.projectlombok:lombok:$lombokVersion")
  testAnnotationProcessor("org.projectlombok:lombok:$lombokVersion")
}

javafx {
  version = "18.0.1"
  modules = listOf("javafx.controls", "javafx.graphics")
}

tasks {
  test {
    minHeapSize = "1024m"
    maxHeapSize = "1536m"
    useJUnitPlatform()
  }
}

