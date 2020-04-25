package pl.droidsonroids.gradle.jacoco.testkit

import org.gradle.api.GradleException
import org.gradle.api.Project
import java.io.File

internal object Configurations {
    const val jacocoRuntime = "jacocoRuntime"
    const val currentTestRuntime = "testRuntimeOnly"
}

internal object Tasks {
    const val test = "test"
    const val generateJacocoTestKitProperties = "generateJacocoTestKitProperties"
}

internal fun Project.testKitDir() = File(buildDir, "testkit")

internal fun File.ensureParentExists() = with(parentFile) {
    isDirectory || mkdirs() || throw GradleException("Could not create $path")
}
