package messyellie.storagecores.coreplugin

import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.project

fun DependencyHandlerScope.addBaseDependency() {
    add("api", project(path = ":base", configuration = "namedElements"))
    add("clientImplementation", project(":base").dependencyProject.sourceSets.getByName("client").output)
}

private val Project.sourceSets: SourceSetContainer get() {
    return extensions.getByName("sourceSets") as SourceSetContainer
}