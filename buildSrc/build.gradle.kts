plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()

    maven {
        name = "Fabric Maven"
        url = uri("https://maven.fabricmc.net/")
    }

    maven {
        name = "Architectury Maven"
        url = uri("https://maven.architectury.dev/")
    }

    maven {
        name = "NeoForge Maven"
        url = uri("https://maven.neoforged.net/releases")
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.10")
    implementation("dev.architectury:architectury-loom:1.2-SNAPSHOT")
    implementation("io.github.juuxel:loom-vineflower:1.11.0")
    implementation("com.google.code.gson:gson:2.10.1")
}