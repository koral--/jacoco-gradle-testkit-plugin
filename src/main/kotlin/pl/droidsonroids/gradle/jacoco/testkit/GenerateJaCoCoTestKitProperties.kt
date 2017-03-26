package pl.droidsonroids.gradle.jacoco.testkit

import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import pl.droidsonroids.gradle.jacoco.testkit.Configurations.jacocoRuntime
import java.io.File

open class GenerateJaCoCoTestKitProperties : DefaultTask() {
    @OutputFile
    val outputFile: File = File(project.testKitDir(), "testkit-gradle.properties")
    @Input
    val jacocoRuntimeConfiguration: Configuration = project.configurations.getByName(jacocoRuntime)

    @TaskAction
    fun createJacocoProperties() {
        outputFile.ensureParentExists()
        val jacocoRuntimePath = jacocoRuntimeConfiguration.asPath
        val jacocoDestFile = "${project.buildDir}/jacoco/test.exec"
        outputFile.writeText("org.gradle.jvmargs:-javaagent:$jacocoRuntimePath=destfile=$jacocoDestFile")
    }
}