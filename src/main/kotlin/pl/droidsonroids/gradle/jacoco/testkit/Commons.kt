package pl.droidsonroids.gradle.jacoco.testkit

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.util.GradleVersion
import java.io.File
import java.util.Locale

internal object Configurations {
    val jacocoRuntime = "jacocoRuntime"
    val currentTestRuntime = when {
        GradleVersion.current() >= GradleVersion.version("3.4") -> "testRuntimeOnly"
        else -> "testRuntime"
    }
}

internal object Tasks {
    val test = "test"
    val generateJacocoTestKitProperties = "generateJacocoTestKitProperties"
}

internal fun Project.testKitDir() = File(buildDir, "testkit")

internal fun File.ensureParentExists() = with(parentFile) {
    isDirectory || mkdirs() || throw GradleException("Could not create $path")
}

internal fun isCurrentOsWindows() =
        System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("windows")