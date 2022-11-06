version = "1.1.0"

javafx {
  version = "18.0.1"
  modules("javafx.controls", "javafx.graphics")
}

publishing {
  publications {
    create<MavenPublication>("maven") {
      groupId = group as String
      artifactId = "storm-core"
      version = version

      from(components["java"])
    }
  }
}