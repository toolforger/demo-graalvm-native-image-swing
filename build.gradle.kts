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

dependencies {
    runtimeOnly(fileTree("src/main/jars") { include("*.jar") })
}

application {
    mainClass.set("SwingSet2")
    applicationDefaultJvmArgs = listOf("-agentlib:native-image-agent=config-output-dir=${projectDir}/src/graalvm")
}

graalvmNative {
    toolchainDetection = true
    binaries {
        named("main") {
            imageName.set("swingset2")
            mainClass.set(application.mainClass)
            buildArgs.addAll(
                // Build args taken from https://www.praj.in/posts/2021/compiling-swing-apps-ahead-of-time/
                "--no-fallback",
                "-H:ConfigurationFileDirectories=${projectDir}/src/graalvm",
                "-Djava.awt.headless=false",
                "-J-Xmx7G",
            )
        }
    }
}
