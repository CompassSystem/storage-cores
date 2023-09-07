pluginManagement {
    repositories {
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
        gradlePluginPortal()
    }
}

rootProject.name = "storage-cores"

include("base")
include("chests")
include("barrels")