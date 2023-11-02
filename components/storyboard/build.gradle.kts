version = "1.0.0"

dependencies {
  api(project(":components:core"))
}

javafx {
  version = "18.0.1"
  modules = listOf("javafx.graphics", "javafx.media")
}

publishing {
  publications {
    create<MavenPublication>("maven") {
      groupId = group as String
      artifactId = "storyboard"
      version = version

      from(components["kotlin"])
    }
  }
}

tasks {
  task<JavaExec>("storyboardTest") {
    dependsOn(compileKotlin, compileTestKotlin)

    group = "Execution"
    description = "Tests the StoryBoardEngine"
    mainClass.set("org.storm.storyboard.StoryBoardEngineTest")
  }
}
