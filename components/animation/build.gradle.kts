import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

version = "1.1.0"

plugins {
  kotlin("jvm") version "1.8.0"
}

javafx {
  version = "18.0.1"
  modules = listOf("javafx.graphics")
}

dependencies {
  implementation(project(":components:core"))
  testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
}

publishing {
  publications {
    create<MavenPublication>("maven") {
      groupId = group as String
      artifactId = "storm-animations"
      version = version

      from(components["kotlin"])
    }
  }
}

tasks {
  task<JavaExec>("animationTest") {
    setupJavaFx(this)

    group = "Execution"
    description = "Simple test for sprite animations and animation looping"
    mainClass.set("org.storm.animations.AnimationTest")
  }

  withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
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
