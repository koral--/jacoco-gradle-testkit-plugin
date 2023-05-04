package pl.droidsonroids.gradle.jacoco.testkit

import org.gradle.api.Project
import java.io.File

internal object Configurations {
    const val jacocoRuntime = "jacocoRuntime"
}

internal fun Project.testKitDir(taskName: String = "test") = File(buildDir, "testkit/$taskName")

internal fun generatePropertiesTaskName(taskName: String) =
    "generateJacoco${taskName.replaceFirstChar(Char::uppercase)}KitProperties"
