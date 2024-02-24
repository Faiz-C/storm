version = "1.1.0"

javafx {
  version = "21.0.2"
  modules = listOf("javafx.graphics")
}

dependencies {
  val junitVersion = "5.9.0"

  api(project(":components:core"))

  testImplementation("org.mockito:mockito-core:4.10.0")
  testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
  testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
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
  test {
    useJUnitPlatform()
  }

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
    exec.classpath(sourceSets.main.get().runtimeClasspath)
    exec.classpath(sourceSets.test.get().runtimeClasspath)

    // For some reason tasks are ignored by the JavaFx Plugin (because why not) so we have to
    // do what the plugin does ourselves
    exec.jvmArgs = listOf(
      "--module-path", exec.classpath.asPath,
      "--add-modules", "javafx.graphics",
      "-Djavafx.animation.fullspeed=true"
    )
  }
}

