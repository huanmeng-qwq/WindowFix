import gg.meza.stonecraft.mod

plugins {
    id("gg.meza.stonecraft")
}

modSettings {
    runDirectory = rootProject.layout.projectDirectory.dir("run")
}

val currentLoader: String = property("loom.platform") as String

dependencies {
}

tasks.processResources {
    val currentLoader = mod.loader
    if (currentLoader != "fabric") {
        exclude("fabric.mod.json")
    }

    if (currentLoader == "neoforge") {
        exclude("META-INF/mods.toml")
    } else {
        exclude("META-INF/neoforge.mod.toml")
    }
}