package pl.droidsonroids.gradle.jacoco.testkit

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.testing.jacoco.plugins.JacocoPlugin.DEFAULT_JACOCO_VERSION
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import pl.droidsonroids.gradle.jacoco.testkit.Configurations.currentTestRuntime
import pl.droidsonroids.gradle.jacoco.testkit.Configurations.jacocoRuntime
import pl.droidsonroids.gradle.jacoco.testkit.Tasks.generateJacocoTestKitProperties
import pl.droidsonroids.gradle.jacoco.testkit.Tasks.test

class JaCoCoTestKitPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        with(project) {
            configurations.maybeCreate(jacocoRuntime)
                    .setVisible(false)
                    .description = "JaCoCo agent for TestKit"

            afterEvaluate {
                val jacocoVersion = extensions.findByType(JacocoPluginExtension::class.java)?.toolVersion ?: DEFAULT_JACOCO_VERSION
                dependencies.add(jacocoRuntime, "org.jacoco:org.jacoco.agent:$jacocoVersion:runtime")
            }

            configurations.configureEach {
                if (it.name == currentTestRuntime) {
                    dependencies.add(currentTestRuntime, files(testKitDir()))
                }
            }

            val jacocoTestKitPropertiesTask = tasks.register(generateJacocoTestKitProperties, GenerateJaCoCoTestKitProperties::class.java)

            tasks.configureEach {
                if (it.name == test) {
                    it.dependsOn(jacocoTestKitPropertiesTask)
                }
            }
        }
    }
}
