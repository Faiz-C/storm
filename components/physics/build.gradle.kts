import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

version = "1.1.0"

plugins {
  kotlin("jvm") version "1.8.0"
}

dependencies {
  val junitVersion = "5.9.0"

  api(project(":components:core"))
  api("org.apache.commons:commons-math3:3.6.1")
  api("com.google.guava:guava:31.1-jre")

  testImplementation("org.mockito:mockito-core:4.10.0")
  testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
  testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
  implementation(kotlin("stdlib-jdk8"))
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
    setupJavaFx(this)

    group = "Execution"
    description = "Particle test and visual showcase of Quadrant Tree"
    mainClass.set("org.storm.physics.visual.ParticleTest")
  }


  task<JavaExec>("atRestTest") {
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
      artifactId = "storm-physics"
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
      "--add-modules", "javafx.graphics"
    )
  }
}

tasks {
  withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "18"
  }
}

