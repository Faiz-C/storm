version = "1.1.0"

javafx {
    version = "21.0.2"
    modules = listOf("javafx.graphics", "javafx.media")
}

tasks {
    task<JavaExec>("mediaSoundTest") {
        dependsOn(compileKotlin, compileTestKotlin)
        setupJavaFx(this)

        group = "Execution"
        description = "Test to check if the Media type is working correctly"
        mainClass.set("org.storm.sound.types.MediaSoundTest")
    }

    task<JavaExec>("soundManagerTest") {
        dependsOn(compileKotlin, compileTestKotlin)
        setupJavaFx(this)

        group = "Execution"
        description = "Test for SoundManager"
        mainClass.set("org.storm.sound.SoundManagerTest")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = group as String
            artifactId = "sound"
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

