version = "1.1.0"

dependencies {
  implementation(project(":components:core"))
}

javafx {
  version = "18.0.1"
  modules = listOf("javafx.controls", "javafx.graphics")
}

publishing {
  publications {
    create<MavenPublication>("maven") {
      groupId = group as String
      artifactId = "storm-animations"
      version = version

      from(components["java"])
    }
  }
}
