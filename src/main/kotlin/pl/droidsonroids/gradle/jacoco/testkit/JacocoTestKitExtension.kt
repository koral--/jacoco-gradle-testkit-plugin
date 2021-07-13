package pl.droidsonroids.gradle.jacoco.testkit

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import org.gradle.testing.jacoco.tasks.JacocoReport
import java.io.File

open class JacocoTestKitExtension(private val project: Project) {
    val jacocoRuntimePathProvider: Provider<String> = project.provider { project.configurations.getByName(Configurations.jacocoRuntime).asPath }

    fun applyTo(configurationRuntime: String, taskProvider: TaskProvider<Task>) {
        with(project) {
            val destinationFile = taskProvider.map { task ->
                val extension = task.extensions.getByType(JacocoTaskExtension::class.java)
                val file = checkNotNull(extension.destinationFile) { "destinationFile is missing on task $name jacoco extension" }

                File(file.parentFile, "${file.nameWithoutExtension}-gradle-runner.${file.extension}")
            }

            val jacocoTestKitPropertiesTask = tasks.register(
                generatePropertiesTaskName(taskProvider.name),
                GenerateJaCoCoTestKitProperties::class.java
            ) {
                it.outputFile = File(buildDir, "testkit/${taskProvider.name}/testkit-gradle.properties")
                it.destinationFile.set(destinationFile.get().path)
                it.jacocoRuntimePath.set(jacocoRuntimePathProvider)
            }

            dependencies.add(configurationRuntime, files(testKitDir(taskProvider.name)))
            taskProvider.configure { it.dependsOn(jacocoTestKitPropertiesTask) }

            tasks.withType(JacocoReport::class.java)
                .matching { it.name == "jacoco${taskProvider.name.capitalize()}Report" }
                .all { it.executionData(destinationFile) }
        }
    }

    fun applyTo(configurationRuntime: String, task: Task) = applyTo(configurationRuntime, project.provider { task } as TaskProvider<Task>)
}