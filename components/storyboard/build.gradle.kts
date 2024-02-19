version = "1.0.0"

dependencies {
  api(project(":components:core"))
  api(project(":components:animation"))
  api(project(":components:sound"))
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
    setupJavaFx(this)

    group = "Execution"
    description = "Tests the StoryBoardEngine"
    mainClass.set("org.storm.storyboard.StoryBoardEngineTest")
  }
}

fun setupJavaFx(exec: JavaExec) {
  exec.doFirst {
    // Setup our class paths
    sourceSets.main.configure {
      exec.classpath(this.runtimeClasspath.asPath)
    }

    sourceSets.test.configure {
      exec.classpath(this.runtimeClasspath.asPath)
    }

    // For some reason tasks are ignored by the JavaFx Plugin (because why not) so we have to
    // do what the plugin does ourselves
    exec.jvmArgs = listOf(
      "--module-path", exec.classpath.asPath,
      "--add-modules", "javafx.graphics,javafx.media"
    )
  }
}
