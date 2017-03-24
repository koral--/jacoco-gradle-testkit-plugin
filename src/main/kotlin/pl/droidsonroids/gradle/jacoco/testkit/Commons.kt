package pl.droidsonroids.gradle.jacoco.testkit

import org.gradle.api.GradleException
import org.gradle.api.Project
import pl.droidsonroids.gradle.jacoco.testkit.Configurations.testRuntime
import java.io.File

internal object Configurations {
    val jacocoRuntime = "jacocoRuntime"
    val testRuntime = "testRuntime"
}

internal object Tasks {
    val test = "test"
    val generateJacocoTestKitProperties = "generateJacocoTestKitProperties"
}

internal fun Project.testKitDirectory() = File(buildDir, "testkit")

internal fun Project.createTestKitRuntimeDependency() =
        dependencies.add(testRuntime, files(testKitDirectory()))

internal fun File.ensureParentDirectoryExists() = with(parentFile) {
    isDirectory || mkdirs() || throw GradleException("Could not create $path")
}