plugins {
    `java-library`
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

allprojects {
    apply(plugin = "java-library")
}

publishing {
    repositories {
        maven("https://mvn.tetratau.net/releases") {
            credentials(PasswordCredentials::class)
            name = "tetratau"
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }

    publications {
        create("installer", MavenPublication::class) {
            pom {
                name.set("toki-installer")
                description.set("A library for installing toki on paper forks or older paper versions")
                url.set("https://github.com/TetraTau/toki-installer")
                inceptionYear.set("2023")

                from(components.named("java").get())
            }
        }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

tasks {
    java {
        withSourcesJar()
    }

    withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(11)
    }
    withType<Javadoc> {
        options.encoding = Charsets.UTF_8.name()
    }
    withType<ProcessResources> {
        filteringCharset = Charsets.UTF_8.name()
    }
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