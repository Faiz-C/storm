version = "1.1.0"

dependencies {
  api(project(":components:core"))
  api(project(":components:physics"))
}

javafx {
  version = "20.0.2"
  modules = listOf("javafx.graphics")
}

tasks {
  task<JavaExec>("backgroundLayerTest") {
    dependsOn(compileKotlin, compileTestKotlin)
    setupJavaFx(this)

    group = "Execution"
    description = "Tests the BackgroundLayer"
    mainClass.set("org.storm.maps.layer.BackgroundLayerTest")
  }

  task<JavaExec>("tileLayerTest") {
    dependsOn(compileKotlin, compileTestKotlin)
    setupJavaFx(this)

    group = "Execution"
    description = "Tests the TileLayer"
    mainClass.set("org.storm.maps.layer.TileLayerTest")
  }

  task<JavaExec>("tileSetTest") {
    dependsOn(compileKotlin, compileTestKotlin)
    setupJavaFx(this)

    group = "Execution"
    description = "Tests the TileSet"
    mainClass.set("org.storm.maps.tile.TileSetTest")
  }

  task<JavaExec>("mapTest") {
    dependsOn(compileKotlin, compileTestKotlin)
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
      artifactId = "maps"
      version = version

      from(components["kotlin"])
    }
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
      "--add-modules", "javafx.graphics,javafx.media"
    )
  }
}

