version = "1.1.0"

dependencies {
    api(project(":components:core"))
    api(project(":components:physics"))
    api(project(":components:sound"))
}

javafx {
    version = "21.0.2"
    modules = listOf("javafx.graphics", "javafx.media")
}

tasks {
    task<JavaExec>("engineTest") {
        dependsOn(compileKotlin, compileTestKotlin)
        setupJavaFx(this)

        group = "Execution"
        description = "A more involved test which tests the storm engines many components and uses multiple states"
        mainClass.set("org.storm.engine.StormEngineTest")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = group as String
            artifactId = "engine"
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
            "--add-modules", "javafx.graphics,javafx.media",
            "-Djavafx.animation.fullspeed=true"
        )
    }
}

