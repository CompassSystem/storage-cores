import messyellie.storagecores.coreplugin.addBaseDependency

plugins {
    id("core-plugin")
}

project.evaluationDependsOn(":base")

dependencies {
    addBaseDependency()
}