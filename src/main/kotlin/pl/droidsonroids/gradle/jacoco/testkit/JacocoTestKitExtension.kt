package pl.droidsonroids.gradle.jacoco.testkit

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import java.io.File

open class JacocoTestKitExtension(private val project: Project) {
    val jacocoRuntimePathProvider: Provider<String> = project.provider { project.configurations.getByName(Configurations.jacocoRuntime).asPath }

    fun applyTo(configurationRuntime: String, taskProvider: TaskProvider<Task>) {
        with(project) {
            val jacocoTestKitPropertiesTask = tasks.register(
                generatePropertiesTaskName(taskProvider.name),
                GenerateJaCoCoTestKitProperties::class.java
            ) {
                it.outputFile = File(buildDir, "testkit/${taskProvider.name}/testkit-gradle.properties")
                it.destinationFile.set(
                    taskProvider.map {
                        task -> task.extensions.getByType(JacocoTaskExtension::class.java).destinationFile!!.path
                    }.get()
                )
                it.jacocoRuntimePath.set(jacocoRuntimePathProvider)
            }

            dependencies.add(configurationRuntime, files(testKitDir(taskProvider.name)))
            taskProvider.configure { it.dependsOn(jacocoTestKitPropertiesTask) }
        }
    }

    fun applyTo(configurationRuntime: String, task: Task) = applyTo(configurationRuntime, project.provider { task } as TaskProvider<Task>)
}