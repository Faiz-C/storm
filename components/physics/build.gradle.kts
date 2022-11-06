version = "1.1.0"

val junitVersion = "5.6.0"

dependencies {
  api(project(":components:core"))
  api("org.apache.commons:commons-math3:3.6.1")
  api("com.google.guava:guava:31.1-jre")

  testImplementation("org.mockito:mockito-core:4.8.0")
  testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
  testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
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

publishing {
  publications {
    create<MavenPublication>("maven") {
      groupId = group as String
      artifactId = "storm-physics"
      version = version

      from(components["java"])
    }
  }
}

