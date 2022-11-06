version = "1.1.0"

dependencies {
  implementation(project(":components:core"))
  implementation(project(":components:physics"))
  implementation(project(":components:sound"))
}

javafx {
  version = "18.0.1"
  modules = listOf("javafx.controls", "javafx.graphics", "javafx.media")
}

publishing {
  publications {
    create<MavenPublication>("maven") {
      groupId = group as String
      artifactId = "storm-engine"
      version = version

      from(components["java"])
    }
  }
}
