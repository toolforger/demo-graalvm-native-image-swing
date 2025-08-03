plugins {
    id("java")
    id("org.graalvm.buildtools.native") version "0.11.0"
    // Provides the "run" target.
    // The build script also uses this plugin to define the application name.
    id("application")
}

// Not really relevant because this is not going to be published on a Maven repository.
// Leaving it in because Gradle plugins tend to require this.
group = "org.toolforger.demos.graalvm"
version = "1.0"

repositories {
    mavenCentral()
}

// Not really relevant because we don't have tests.
// Keeping it because projects copying this build file usually need this.
dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}


application {
    mainClass.set("org.toolforger.demos.graalvm.Main")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = application.mainClass
        archiveFileName.set("hello-world.jar")
    }
}

graalvmNative {
    toolchainDetection = true
    binaries {
        named("main") {
            imageName.set("hello-world")
            mainClass.set(application.mainClass)
            buildArgs.add("-Djava.awt.headless=false")
        }
    }
}
