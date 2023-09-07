import net.fabricmc.loom.task.RemapJarTask
import messyellie.storagecores.coreplugin.JsonNormalizerReader

plugins {
    `java-library`
    id("dev.architectury.loom")
    id("io.github.juuxel.loom-vineflower")
}

val modId = "storagecores_${project.name}"
val javaVersion = JavaVersion.VERSION_17
val usesDatagen = true
val producesReleaseArtifact = true

loom {
    silentMojangMappingsLicense()

    findProperty("access_widener_path")?.let {
        accessWidenerPath = file(it)
    }

    mixin {
        defaultRefmapName = "$modId.refmap.json"
    }

    splitEnvironmentSourceSets()

    mods {
        create(modId) {
            sourceSet("main")
            sourceSet("client")
        }
    }
}

repositories {
    maven {
        name = "ParchmentMC Maven"
        url = uri("https://maven.parchmentmc.org")
    }
}

dependencies {
    minecraft("com.mojang:minecraft:1.20.1")

    mappings(loom.layered {
        officialMojangMappings()

        parchment("org.parchmentmc.data:parchment-1.20.1:2023.09.03@zip")
    })

    modImplementation("net.fabricmc:fabric-loader:0.14.21")
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.88.1+1.20.1")

    compileOnly("org.jetbrains:annotations:24.0.1")
}

sourceSets {
    named("main") {
        if (usesDatagen) {
            resources.srcDir("src/generated/resources")
        }
    }
}

tasks {
    processResources {
        inputs.properties(mutableMapOf("version" to version))

        filesMatching("fabric.mod.json") {
            expand(inputs.properties)
        }

        exclude(".cache/*")
    }

    withType(JavaCompile::class.java).configureEach {
        options.encoding = "UTF-8"
        options.release = javaVersion.ordinal + 1
    }

    getByName<Jar>("jar") {
        from("LICENSE")

        if (usesDatagen) {
            exclude("**/datagen")
        }

        if (producesReleaseArtifact) {
            archiveClassifier = "dev"
        }
    }

    getByName<RemapJarTask>("remapJar") {
        if (producesReleaseArtifact) {
            injectAccessWidener = true

            archiveClassifier = "fat"
        }
    }

    create("minJar", Jar::class.java) {
        inputs.files(getByName("remapJar").outputs.files)

        duplicatesStrategy = DuplicatesStrategy.FAIL

        inputs.files.forEach {
            if (it.extension == "jar") {
                this.from(zipTree(it)) {
                    exclude("**/MANIFEST.MF")
                }
            }
        }

        filesMatching(listOf("**/*.json", "**/*.mcmeta")) {
            filter(JsonNormalizerReader::class.java)
        }

        dependsOn("remapJar")
    }

    build.get().dependsOn("minJar")
}
