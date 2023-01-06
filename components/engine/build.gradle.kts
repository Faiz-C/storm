version = "1.1.0"

dependencies {
  implementation(project(":components:core"))
  implementation(project(":components:physics"))
  implementation(project(":components:sound"))
}

javafx {
  version = "18.0.1"
  modules = listOf("javafx.graphics", "javafx.media")
}

tasks {
  task<JavaExec>("engineTest") {
    dependsOn(compileKotlin, compileTestKotlin)

    setupJavaFx(this)

    group = "Execution"
    description = "A more involved test which tests the storm engines many components and uses multiple states"
    mainClass.set("org.storm.engine.StormTest")
  }
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
      "--add-modules", "javafx.graphics,javafx.media",
      "-Djavafx.animation.fullspeed=true"
    )
  }
}
