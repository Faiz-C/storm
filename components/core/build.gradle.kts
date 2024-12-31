version = "1.1.0"

javafx {
    version = "21.0.2"
    modules("javafx.controls", "javafx.graphics")
}

dependencies {
    implementation("org.reflections:reflections:0.10.2")
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
        description = "Simple test for sprite animations and animation looping"
        mainClass.set("org.storm.core.ui.JfxWindowTest")
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