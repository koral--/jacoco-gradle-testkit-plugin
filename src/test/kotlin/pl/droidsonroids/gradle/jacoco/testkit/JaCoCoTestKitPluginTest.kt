package pl.droidsonroids.gradle.jacoco.testkit

import org.assertj.core.api.Assertions.assertThat
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testing.jacoco.plugins.JacocoPlugin
import org.junit.Before
import org.junit.Test
import pl.droidsonroids.gradle.jacoco.testkit.Configurations.currentTestRuntime
import pl.droidsonroids.gradle.jacoco.testkit.Tasks.test

class JaCoCoTestKitPluginTest {

    private lateinit var project: Project

    @Before
    fun setUp() {
        project = ProjectBuilder.builder().build()
        project.pluginManager.apply(JavaPlugin::class.java)
        project.pluginManager.apply(JacocoPlugin::class.java)
        project.pluginManager.apply(JaCoCoTestKitPlugin::class.java)
    }

    @Test
    fun `properties resource added to testRuntime configuration`() {
        val testTask = project.tasks.named(test)
        project.extensions.getByType(JaCoCoTestKit::class.java).applyTo(currentTestRuntime, testTask)
        assertThat(project.configurations.getByName(currentTestRuntime).allDependencies).isNotEmpty
    }

    @Test
    fun `generateJacocoTestKitProperties task created with dependencies`() {
        val testTask = project.tasks.named(test)
        project.extensions.getByType(JaCoCoTestKit::class.java).applyTo(currentTestRuntime, testTask)
        assertThat(testTask.get().taskDependencies.getDependencies(testTask.get()))
                .contains(project.tasks.getByName(Tasks.generateJacocoTestKitProperties))
    }
}