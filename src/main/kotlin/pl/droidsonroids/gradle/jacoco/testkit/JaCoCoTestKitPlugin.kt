package pl.droidsonroids.gradle.jacoco.testkit

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import org.gradle.testing.jacoco.plugins.JacocoPlugin.DEFAULT_JACOCO_VERSION
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import pl.droidsonroids.gradle.jacoco.testkit.Configurations.jacocoRuntime

open class JaCoCoTestKit(private val project: Project) {
    val jacocoRuntimePathProvider: Provider<String> = project.provider { project.configurations.getByName(jacocoRuntime).asPath }

    fun applyTo(configurationRuntime: String, taskProvider: TaskProvider<Task>) {
        with(project) {
            dependencies.add(configurationRuntime, files(testKitDir()))
            val jacocoTestKitPropertiesTask = tasks.register(
                "generateJacoco${taskProvider.name.capitalize()}KitProperties",
                GenerateJaCoCoTestKitProperties::class.java
            ) {
                it.apply {
                    destinationFile.set(taskProvider.map { task ->
                        task.extensions.getByType(JacocoTaskExtension::class.java).destinationFile!!.path
                    }.get())
                    jacocoRuntimePath.set(jacocoRuntimePathProvider)
                }
            }
            taskProvider.configure {
                it.dependsOn(jacocoTestKitPropertiesTask)
            }
        }
    }
}

class JaCoCoTestKitPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        with(project) {
            extensions.create("jacocoTestKit", JaCoCoTestKit::class.java, project)
            configurations.maybeCreate(jacocoRuntime)
                    .setVisible(false)
                    .description = "JaCoCo agent for TestKit"

            afterEvaluate {
                val jacocoVersion = extensions.findByType(JacocoPluginExtension::class.java)?.toolVersion ?: DEFAULT_JACOCO_VERSION
                dependencies.add(jacocoRuntime, "org.jacoco:org.jacoco.agent:$jacocoVersion:runtime")
            }
        }
    }
}
