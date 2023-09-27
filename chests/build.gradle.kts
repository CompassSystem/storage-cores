import compass_system.storagecores.coreplugin.addBaseDependency

plugins {
    id("core-plugin")
}

project.evaluationDependsOn(":base")

dependencies {
    addBaseDependency()
}