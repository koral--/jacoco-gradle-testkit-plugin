package pl.droidsonroids.gradle.jacoco.testkit

import org.assertj.core.api.Assertions.assertThat
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test
import pl.droidsonroids.gradle.jacoco.testkit.Configurations.currentTestRuntime
import pl.droidsonroids.gradle.jacoco.testkit.Tasks.test

class JaCoCoTestKitPluginTest {

    private lateinit var project: Project

    @Before
    fun setUp() {
        project = ProjectBuilder.builder().build()
        project.pluginManager.apply(JaCoCoTestKitPlugin::class.java)
    }

    @Test
    fun `properties resource added to testRuntime configuration`() {
        project.configurations.create(currentTestRuntime)
        assertThat(project.configurations.getByName(currentTestRuntime).allDependencies).isNotEmpty
    }

    @Test
    fun `generateJacocoTestKitProperties task created with dependencies`() {
        project.tasks.create(test)
        val testTask = project.tasks.getByName(test)
        assertThat(testTask.taskDependencies.getDependencies(testTask))
                .contains(project.tasks.getByName(Tasks.generateJacocoTestKitProperties))
    }
}