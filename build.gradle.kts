plugins {
    `java-library`
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

allprojects {
    apply(plugin = "java-library")
}

project(":application") {
    apply(plugin = "com.github.johnrengelman.shadow")

    dependencies {
       implementation(rootProject)
    }

    tasks {
        shadowJar {
            manifest {
                attributes(
                    "Main-Class" to "net.tetratau.toki.installer.Application"
                )
            }
        }
    }
}