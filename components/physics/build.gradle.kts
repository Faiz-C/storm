version = "1.1.0"

val junitVersion = "5.6.0"

dependencies {
  api(project(":components:core"))
  api("org.apache.commons:commons-math3:3.6.1")
  api("com.google.guava:guava:31.1-jre")

  testImplementation("org.mockito:mockito-core:4.8.0")
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
    doFirst {
      // Setup our class paths
      sourceSets.main.configure {
        this@task.classpath(this.runtimeClasspath.asPath)
      }
      sourceSets.test.configure {
        this@task.classpath(this.runtimeClasspath.asPath)
      }

      // For some reason tasks are ignored by the JavaFx Plugin (because why not) so we have to
      // do what the plugin does ourselves
      jvmArgs = listOf(
        "--module-path", this@task.classpath.asPath,
        "--add-modules", "javafx.graphics"
      )
    }

    group = "Execution"
    description = "Particle test and visual showcase of Quadrant Tree"
    mainClass.set("org.storm.physics.visual.ParticleTest")
  }
}

publishing {
  publications {
    create<MavenPublication>("maven") {
      groupId = group as String
      artifactId = "storm-physics"
      version = version

      from(components["java"])
    }
  }
}

