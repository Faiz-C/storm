version = "1.1.0"

javafx {
  version = "18.0.1"
  modules("javafx.controls", "javafx.graphics")
}

dependencies {
  implementation("org.reflections:reflections:0.10.2")
}

publishing {
  publications {
    create<MavenPublication>("maven") {
      groupId = group as String
      artifactId = "core"
      version = version

      from(components["kotlin"])
    }
  }
}
