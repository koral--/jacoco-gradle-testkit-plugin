package pl.droidsonroids.gradle.jacoco.testkit

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.testing.jacoco.plugins.JacocoPlugin.DEFAULT_JACOCO_VERSION
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import pl.droidsonroids.gradle.jacoco.testkit.Configurations.jacocoRuntime

class JaCoCoTestKitPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        with(project) {
            val extension = extensions.create("jacocoTestKit", JacocoTestKitExtension::class.java, project)
            configurations.maybeCreate(jacocoRuntime)
                    .setVisible(false)
                    .description = "JaCoCo agent for TestKit"

            afterEvaluate {
                val jacocoVersion = extensions.findByType(JacocoPluginExtension::class.java)?.toolVersion ?: DEFAULT_JACOCO_VERSION
                dependencies.add(jacocoRuntime, "org.jacoco:org.jacoco.agent:$jacocoVersion:runtime")
            }

            pluginManager.withPlugin("java") {
                extension.applyTo(JavaPlugin.TEST_RUNTIME_ONLY_CONFIGURATION_NAME, tasks.named(JavaPlugin.TEST_TASK_NAME))
            }
        }
    }
}
