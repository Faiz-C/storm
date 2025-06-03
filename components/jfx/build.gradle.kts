version = "2.0.0"

javafx {
    version = "21.0.2"
    modules("javafx.graphics", "javafx.media")
}

dependencies {
    api(project(":components:core"))
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = group as String
            artifactId = "core"
            version = version

            from(components["kotlin"])
        }
    }
}

tasks {
    task<JavaExec>("jfxWindowTest") {
        dependsOn(compileKotlin, compileTestKotlin)

        setupJavaFx(this)

        group = "Execution"
        description = "Simple test for window creations"
        mainClass.set("org.storm.impl.jfx.graphics.JfxWindowTest")
    }

    task<JavaExec>("jfxSoundTest") {
        dependsOn(compileKotlin, compileTestKotlin)
        setupJavaFx(this)

        group = "Execution"
        description = "Test to check if the Jfx type is working correctly"
        mainClass.set("org.storm.impl.jfx.sound.JfxSoundTest")
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