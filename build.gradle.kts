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
    implementation(fileTree("src/main/jars") { include("*.jar") })
}

application {
    mainClass.set("JideDemos")
    applicationDefaultJvmArgs = listOf(
        "--add-exports", "java.desktop/com.sun.java.swing.plaf.windows=ALL-UNNAMED",
        "--add-exports", "java.desktop/sun.swing=ALL-UNNAMED",
        "--add-exports", "java.base/sun.security.action=ALL-UNNAMED",
        "--add-exports", "java.desktop/sun.awt.windows=ALL-UNNAMED",
        "--add-exports", "java.desktop/sun.awt.image=ALL-UNNAMED",
        "--add-exports", "java.desktop/sun.awt.shell=ALL-UNNAMED",
        "--add-opens", "java.desktop/java.awt=ALL-UNNAMED",
        "--add-opens", "java.desktop/java.awt.color=ALL-UNNAMED",
        "--add-opens", "java.desktop/javax.swing=ALL-UNNAMED",
        "--add-opens", "java.base/java.lang=ALL-UNNAMED",
        "--add-opens", "java.base/java.util=ALL-UNNAMED",
        "--add-opens", "java.base/java.text=ALL-UNNAMED",
        "--add-opens", "java.base/java.math=ALL-UNNAMED",
        "--add-opens", "jdk.management/com.sun.management.internal=ALL-UNNAMED",
        "--add-opens", "java.xml/com.sun.org.apache.xml.internal.serialize=ALL-UNNAMED",
        "-agentlib:native-image-agent=config-output-dir=${projectDir}/src/graalvm"
    )
}

graalvmNative {
    toolchainDetection = true
    binaries {
        named("main") {
            imageName.set("jide_demo")
            mainClass.set(application.mainClass)
            buildArgs.addAll(
                // Build args taken from https://www.praj.in/posts/2021/compiling-swing-apps-ahead-of-time/
                "--no-fallback",
                "-H:ConfigurationFileDirectories=${projectDir}/src/graalvm",
                "-Djava.awt.headless=false",
                // "-J-Xmx7G",
                "-J-XX:ActiveProcessorCount=6",
            )
            jvmArgs(
                // You'd want to put this list into a variable for DRY
                "--add-exports", "java.desktop/com.sun.java.swing.plaf.windows=ALL-UNNAMED",
                "--add-exports", "java.desktop/sun.swing=ALL-UNNAMED",
                "--add-exports", "java.base/sun.security.action=ALL-UNNAMED",
                "--add-exports", "java.desktop/sun.awt.windows=ALL-UNNAMED",
                "--add-exports", "java.desktop/sun.awt.image=ALL-UNNAMED",
                "--add-exports", "java.desktop/sun.awt.shell=ALL-UNNAMED",
                "--add-opens", "java.desktop/java.awt=ALL-UNNAMED",
                "--add-opens", "java.desktop/java.awt.color=ALL-UNNAMED",
                "--add-opens", "java.desktop/javax.swing=ALL-UNNAMED",
                "--add-opens", "java.base/java.lang=ALL-UNNAMED",
                "--add-opens", "java.base/java.util=ALL-UNNAMED",
                "--add-opens", "java.base/java.text=ALL-UNNAMED",
                "--add-opens", "java.base/java.math=ALL-UNNAMED",
                "--add-opens", "jdk.management/com.sun.management.internal=ALL-UNNAMED",
                "--add-opens", "java.xml/com.sun.org.apache.xml.internal.serialize=ALL-UNNAMED",
            )
        }
    }
}
