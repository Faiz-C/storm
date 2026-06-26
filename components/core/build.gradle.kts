import org.gradle.kotlin.dsl.register

version = "2.2.0"

javafx {
    version = "26"
    modules("javafx.graphics", "javafx.media")
    configuration = "testImplementation"
}

java {
    withSourcesJar()
    withJavadocJar()
}

dependencies {
    implementation("org.reflections:reflections:0.10.2")
    testImplementation(project(":components:jfx"))
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = group as String
            artifactId = "core"
            version = version

            from(components["kotlin"])
            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])
        }
    }
}

tasks {
    register<JavaExec>("soundManagerTest", fun JavaExec.() {
        dependsOn(compileKotlin, compileTestKotlin)
        setupJavaFx(this)

        group = "Execution"
        description = "Test for SoundManager"
        mainClass.set("org.storm.core.sound.SoundManagerTest")
    })

    register<JavaExec>("animationTest", fun JavaExec.() {
        dependsOn(compileKotlin, compileTestKotlin)

        setupJavaFx(this)

        group = "Execution"
        description = "Simple test for sprite animations and animation looping"
        mainClass.set("org.storm.core.graphics.animation.AnimationTest")
    })
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
