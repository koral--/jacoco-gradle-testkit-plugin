package pl.droidsonroids.gradle.jacoco.testkit

import org.assertj.core.api.Assertions.assertThat
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testing.jacoco.plugins.JacocoPlugin.DEFAULT_JACOCO_VERSION
import org.junit.Before
import org.junit.Test
import pl.droidsonroids.gradle.jacoco.testkit.Configurations.jacocoRuntime
import pl.droidsonroids.gradle.jacoco.testkit.Configurations.testRuntime
import pl.droidsonroids.gradle.jacoco.testkit.Tasks.test

class JaCoCoTestKitPluginTest {

    lateinit var project: Project

    @Before
    fun setUp() {
        project = ProjectBuilder.builder().build()
        project.pluginManager.apply(JaCoCoTestKitPlugin::class.java)
    }

    @Test
    fun `properties resource added to testRuntime configuration`() {
        project.configurations.create(testRuntime)
        assertThat(project.configurations.getByName(testRuntime).allDependencies).isNotEmpty
    }

    @Test
    fun `jacocoRuntime configuration created with default version`() {
        val dependencies = project.configurations.getByName(jacocoRuntime).allDependencies
        val jacocoRuntimeDependency = dependencies.find { it.group == "org.jacoco" && it.name == "org.jacoco.agent" && it.version == DEFAULT_JACOCO_VERSION }
        assertThat(jacocoRuntimeDependency).isNotNull()
    }

    @Test
    fun `generateJacocoTestKitProperties task created with dependencies`() {
        project.tasks.create(test)
        val testTask = project.tasks.getByName(test)
        assertThat(testTask.taskDependencies.getDependencies(testTask))
                .contains(project.tasks.getByName(Tasks.generateJacocoTestKitProperties))
    }
}