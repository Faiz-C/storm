version = "1.1.0"

javafx {
  version = "18.0.1"
  modules = listOf("javafx.controls", "javafx.graphics", "javafx.media")
}

publishing {
  publications {
    create<MavenPublication>("maven") {
      groupId = group as String
      artifactId = "storm-sound"
      version = version

      from(components["java"])
    }
  }
}
