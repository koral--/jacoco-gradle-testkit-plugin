package pl.droidsonroids.gradle.jacoco.testkit

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.testing.jacoco.plugins.JacocoPlugin.DEFAULT_JACOCO_VERSION
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import pl.droidsonroids.gradle.jacoco.testkit.Configurations.jacocoRuntime
import pl.droidsonroids.gradle.jacoco.testkit.Configurations.testRuntime
import pl.droidsonroids.gradle.jacoco.testkit.Tasks.generateJacocoTestKitProperties
import pl.droidsonroids.gradle.jacoco.testkit.Tasks.test

class JaCoCoTestKitPlugin : Plugin<Project> {

    override fun apply(project: Project) = with(project) {
        val jacocoVersion = extensions.findByType(JacocoPluginExtension::class.java)?.toolVersion ?: DEFAULT_JACOCO_VERSION
        val jacocoTestKitPropertiesTask = tasks.create(generateJacocoTestKitProperties, GenerateJaCoCoTestKitProperties::class.java)

        configurations.maybeCreate(jacocoRuntime).isVisible = false

        dependencies.add(jacocoRuntime, "org.jacoco:org.jacoco.agent:$jacocoVersion:runtime")

        if (configurations.findByName(testRuntime) != null) {
            createTestKitRuntimeDependency()
        } else {
            configurations.whenObjectAdded {
                if (it.name == testRuntime) {
                    createTestKitRuntimeDependency()
                }
            }
        }

        fun Project.addJaCoCoPropertiesTaskAsDependency() {
            tasks.getByName(test).dependsOn(jacocoTestKitPropertiesTask)
        }

        if (tasks.findByName(test) != null) {
            addJaCoCoPropertiesTaskAsDependency()
        } else {
            tasks.whenObjectAdded {
                if (it.name == test) {
                    addJaCoCoPropertiesTaskAsDependency()
                }
            }
        }
        Unit
    }
}
