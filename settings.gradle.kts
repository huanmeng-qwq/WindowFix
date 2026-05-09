pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.kikugie.dev/releases")
        maven("https://maven.kikugie.dev/snapshots")
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev")
        maven("https://maven.minecraftforge.net")
        maven("https://maven.neoforged.net/releases/")
    }
}

plugins {
    id("gg.meza.stonecraft") version "1.10.+"
    id("dev.kikugie.stonecutter") version "0.9+"
}

stonecutter {
    centralScript = "build.gradle.kts"
    kotlinController = true
    shared {
        fun mc(version: String, vararg loaders: String) {
            for (it in loaders) {
                version("${version}-$it", version)
            }
        }

        // ver     fabric   forge   neo
        // 1.20.1  ~1.21.11 ~1.20.4  x
        // 1.21.1     x     ~1.21.5  ~1.21.8
        // 1.21.6     x     ~1.21.8  x
        // 1.21.9     x     ~1.21.11 ~1.21.11
        mc("1.20.1", "fabric", "forge")
        mc("1.21.1", "forge", "neoforge")
        mc("1.21.6", "forge")
        mc("1.21.9", "forge", "neoforge")
        mc("26.1", "neoforge", "fabric")

        vcsVersion = "1.20.1-forge"
    }
    create(rootProject)
}

rootProject.name = "windowfix"
