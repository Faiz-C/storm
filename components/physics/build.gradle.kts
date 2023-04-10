version = "1.1.0"

dependencies {
  val junitVersion = "5.9.0"

  api(project(":components:core"))
  api("org.apache.commons:commons-math3:3.6.1")
  api("com.google.guava:guava:31.1-jre")

  testImplementation("org.mockito:mockito-core:4.10.0")
  testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
  testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
}

javafx {
  version = "18.0.1"
  modules("javafx.graphics")
}

tasks {
  test {
    minHeapSize = "1G"
    maxHeapSize = "2G"
    useJUnitPlatform()
  }

  task<JavaExec>("particleTest") {
    dependsOn(compileKotlin, compileTestKotlin)
    setupJavaFx(this)

    group = "Execution"
    description = "Particle test and visual showcase of Quadrant Tree"
    mainClass.set("org.storm.physics.visual.ParticleTest")
  }

  task<JavaExec>("multiPartBoundaryTest") {
    dependsOn(compileKotlin, compileTestKotlin)
    setupJavaFx(this)

    group = "Execution"
    description = "A test and visual showcase of using multiple shapes to construct a more complex boundary for an Entity"
    mainClass.set("org.storm.physics.visual.MultiPartBoundaryTest")
  }

  task<JavaExec>("atRestTest") {
    dependsOn(compileKotlin, compileTestKotlin)
    setupJavaFx(this)

    group = "Execution"
    description = "A simple test which ensures that objects can fall into a 'rest' state and stop needing collision checks"
    mainClass.set("org.storm.physics.visual.VisualAtRestTest")
  }
}

publishing {
  publications {
    create<MavenPublication>("maven") {
      groupId = group as String
      artifactId = "physics"
      version = version

      from(components["kotlin"])
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
      "--add-modules", "javafx.graphics"
    )
  }
}



