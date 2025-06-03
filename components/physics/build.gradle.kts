version = "2.0.0"

javafx {
    version = "21.0.2"
    modules("javafx.graphics", "javafx.media")
    configuration = "testImplementation"
}

dependencies {
    api(project(":components:core"))
    api("org.apache.commons:commons-math3:3.6.1")
    //api("com.google.guava:guava:33.0.0-jre")
    testImplementation(project(":components:jfx"))
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
        description =
            "A test and visual showcase of using multiple shapes to construct a more complex boundary for an Entity"
        mainClass.set("org.storm.physics.visual.MultiPartBoundaryTest")
    }

    task<JavaExec>("atRestTest") {
        dependsOn(compileKotlin, compileTestKotlin)
        setupJavaFx(this)

        group = "Execution"
        description =
            "A simple test which ensures that objects can fall into a 'rest' state and stop needing collision checks"
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




