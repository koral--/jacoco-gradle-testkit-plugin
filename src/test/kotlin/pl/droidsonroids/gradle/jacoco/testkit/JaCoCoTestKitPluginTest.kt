package pl.droidsonroids.gradle.jacoco.testkit

import org.assertj.core.api.Assertions.assertThat
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testing.jacoco.plugins.JacocoPlugin
import org.junit.Before
import org.junit.Test
import java.util.*

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
        assertThat(project.configurations.getByName(JavaPlugin.TEST_RUNTIME_ONLY_CONFIGURATION_NAME).allDependencies).isNotEmpty
    }

    @Test
    fun `generateJacocoTestKitProperties task created with dependencies`() {
        val testTask = project.tasks.named(JavaPlugin.TEST_TASK_NAME)
        assertThat(testTask.get().taskDependencies.getDependencies(testTask.get()))
                .contains(project.tasks.getByName("generateJacocoTestKitProperties"))
    }
}