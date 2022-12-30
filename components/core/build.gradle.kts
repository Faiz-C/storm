import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

version = "1.1.0"

plugins {
  kotlin("jvm") version "1.8.0"
}

dependencies {
  implementation(kotlin("stdlib"))
}

javafx {
  version = "18.0.1"
  modules("javafx.controls", "javafx.graphics")
}

publishing {
  publications {
    create<MavenPublication>("maven") {
      groupId = group as String
      artifactId = "storm-core"
      version = version

      from(components["kotlin"])
    }
  }
}

tasks {
  withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "18"
  }
}
