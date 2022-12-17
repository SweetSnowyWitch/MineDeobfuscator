import java.io.ByteArrayOutputStream

plugins {
    id("fabric-loom") version "1.0-SNAPSHOT"
    id("maven-publish")
}

fun getGitCommit(): String {
    val stdout = ByteArrayOutputStream()
    exec {
        commandLine("git", "rev-parse", "--short", "HEAD")
        standardOutput = stdout
    }
    return stdout.toString().trim()
}

val archivesBaseName = "stackdeobfuscator"
version = "1.0.0+fabric.${getGitCommit()}"
group = "dev.booky"

repositories {
    maven("https://jitpack.io/") {
        content {
            includeModule("com.github.LlamaLad7", "MixinExtras")
        }
    }
}

dependencies {
    minecraft("com.mojang:minecraft:1.19.3")
    mappings(loom.officialMojangMappings())

    modImplementation("net.fabricmc:fabric-loader:0.14.11")

    // Useful additions to mixin
    val mixinExtrasVersion = "0.1.1"
    include(implementation("com.github.LlamaLad7:MixinExtras:$mixinExtrasVersion")!!)
    annotationProcessor("com.github.LlamaLad7:MixinExtras:$mixinExtrasVersion")
}

java {
    withSourcesJar()
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

publishing {
    publications.create<MavenPublication>("maven") {
        artifactId = project.name.toLowerCase()
        from(components["java"])
    }
}

tasks {
    processResources {
        inputs.property("version", project.version)
        filesMatching("fabric.mod.json") {
            expand("version" to project.version)
        }
    }

    withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }

    jar {
        from("LICENSE") {
            rename { return@rename "${it}_stackdeobfuscator" }
        }
    }
}

loom {
    accessWidenerPath.set(File(project.rootDir, "src/main/resources/stackdeobfuscator.accesswidener"))
}
