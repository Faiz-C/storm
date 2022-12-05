version = "1.1.0"

dependencies {
  implementation(project(":components:core"))
  implementation(project(":components:physics"))
}

javafx {
  version = "18.0.1"
  modules = listOf("javafx.graphics")
}

tasks {
  task<JavaExec>("backgroundLayerTest") {
    setupJavaFx(this)

    group = "Execution"
    description = "Tests the BackgroundLayer"
    mainClass.set("org.storm.maps.layer.BackgroundLayerTest")
  }

  task<JavaExec>("tileLayerTest") {
    setupJavaFx(this)

    group = "Execution"
    description = "Tests the TileLayer"
    mainClass.set("org.storm.maps.layer.TileLayerTest")
  }

  task<JavaExec>("tileSetTest") {
    setupJavaFx(this)

    group = "Execution"
    description = "Tests the TileSet"
    mainClass.set("org.storm.maps.tile.TileSetTest")
  }

  task<JavaExec>("MapTest") {
    setupJavaFx(this)

    group = "Execution"
    description = "Tests the Map with a couple of layers"
    mainClass.set("org.storm.maps.MapTest")
  }
}

publishing {
  publications {
    create<MavenPublication>("maven") {
      groupId = group as String
      artifactId = "storm-maps"
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
