package pl.droidsonroids.gradle.jacoco.testkit

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.testing.jacoco.plugins.JacocoPlugin.DEFAULT_JACOCO_VERSION
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import pl.droidsonroids.gradle.jacoco.testkit.Configurations.jacocoRuntime
import pl.droidsonroids.gradle.jacoco.testkit.Configurations.testRuntime
import pl.droidsonroids.gradle.jacoco.testkit.Tasks.generateJacocoTestKitProperties
import pl.droidsonroids.gradle.jacoco.testkit.Tasks.test

class JacocoTestKitPlugin : Plugin<Project> {

    override fun apply(project: Project) = with(project) {
        val jacocoVersion = extensions.findByType(JacocoPluginExtension::class.java)?.toolVersion ?: DEFAULT_JACOCO_VERSION
        configurations.maybeCreate(jacocoRuntime).isVisible = false

        dependencies.add(jacocoRuntime, "org.jacoco:org.jacoco.agent:$jacocoVersion:runtime")

        configurations.whenObjectAdded {
            if (it.name == testRuntime) {
                dependencies.add(testRuntime, files(testKitDirectory()))
            }
        }
        val jacocoTestKitPropertiesTask = tasks.create(generateJacocoTestKitProperties, GenerateJacocoTestKitProperties::class.java)
        tasks.whenObjectAdded {
            if (it.name == test) {
                tasks.getByName(test).dependsOn(jacocoTestKitPropertiesTask)
            }
        }
        Unit
    }
}
