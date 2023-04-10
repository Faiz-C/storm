version = "1.1.0"

javafx {
  version = "18.0.1"
  modules = listOf("javafx.graphics")
}

dependencies {
  api(project(":components:core"))
}

publishing {
  publications {
    create<MavenPublication>("maven") {
      groupId = group as String
      artifactId = "animations"
      version = version

      from(components["kotlin"])
    }
  }
}

tasks {
  task<JavaExec>("animationTest") {
    dependsOn(compileKotlin, compileTestKotlin)

    setupJavaFx(this)

    group = "Execution"
    description = "Simple test for sprite animations and animation looping"
    mainClass.set("org.storm.animations.AnimationTest")
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
      "--add-modules", "javafx.graphics"
    )
  }
}
