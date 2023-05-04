package pl.droidsonroids.gradle.jacoco.testkit

import org.assertj.core.api.Assertions.assertThat
import org.gradle.testing.jacoco.plugins.JacocoPlugin
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.util.Properties

class JaCoCoTestKitPluginFunctionalTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Before
    fun setUp() {
        temporaryFolder.newFile("gradle.properties").fillFromResource("testkit-gradle.properties")
    }

    companion object {
        const val generateJacocoTestKitProperties = "generateJacocoTestKitProperties"
    }

    @Test
    fun `gradle properties file generated with default version`() {
        temporaryFolder.newFile("build.gradle").fillFromResource("simple.gradle")
        GradleRunner.create()
            .withProjectDir(temporaryFolder.root)
            .withTestKitDir(temporaryFolder.newFolder())
            .withArguments(generateJacocoTestKitProperties)
            .withPluginClasspath()
            .build()

        val args = readArgsFromProperties()
        assertThat(args)
            .startsWith("\"-javaagent:")
            .contains("=destfile=")
            .contains(JacocoPlugin.DEFAULT_JACOCO_VERSION)
            .endsWith("test.exec\"")
    }

    @Test
    fun `gradle properties file generated with explicit version`() {
        temporaryFolder.newFile("build.gradle").fillFromResource("extension.gradle")
        GradleRunner.create()
            .withProjectDir(temporaryFolder.root)
            .withTestKitDir(temporaryFolder.newFolder())
            .withArguments(generateJacocoTestKitProperties)
            .withPluginClasspath()
            .build()

        val args = readArgsFromProperties()
        assertThat(args)
            .startsWith("\"-javaagent:")
            .contains("=destfile=")
            .contains("0.7.7.201606060606")
            .endsWith(".exec\"")
    }

    @Test
    fun `gradle properties file generated doesn't change between builds`() {
        temporaryFolder.newFile("build.gradle").fillFromResource("extension.gradle")
        val testKitDir: File = temporaryFolder.newFolder()
        val gradleBuilder = GradleRunner.create()
            .withProjectDir(temporaryFolder.root)
            .withTestKitDir(testKitDir)
            .withArguments(generateJacocoTestKitProperties, "--rerun-tasks")
            .withPluginClasspath()

        gradleBuilder.build()
        val firstBuildProperties = readLinesFromProperties()

        Thread.sleep(1000) // we need to wait for a second to be sure a timestamp would have changed
        testKitDir.listFiles()?.forEach { it.delete() }

        gradleBuilder.build()
        val secondBuildProperties = readLinesFromProperties()


        assertThat(secondBuildProperties)
            .isEqualTo(firstBuildProperties)
    }


    @Test
    fun `plugin compatible with Gradle 7_6`() {
        temporaryFolder.newFile("build.gradle").fillFromResource("custom-task.gradle")
        assertThat(
            GradleRunner.create()
                .withGradleVersion("7.6")
                .withProjectDir(temporaryFolder.root)
                .withTestKitDir(temporaryFolder.newFolder())
                .withArguments(generateJacocoTestKitProperties)
                .withPluginClasspath()
                .build()
                .task(":$generateJacocoTestKitProperties")?.outcome
        ).isEqualTo(TaskOutcome.SUCCESS)
    }

    @Test
    fun `plugin compatible with Configuration Cache`() {
        val testKitDir = temporaryFolder.newFolder()
        // https://docs.gradle.org/8.1/userguide/configuration_cache.html#config_cache:not_yet_implemented:testkit_build_with_java_agent
        temporaryFolder.root.resolve("gradle.properties").delete()

        temporaryFolder.newFile("build.gradle").fillFromResource("simple.gradle")
        val initialRun = GradleRunner.create()
            .withProjectDir(temporaryFolder.root)
            .withTestKitDir(testKitDir)
            .withArguments(generateJacocoTestKitProperties, "--configuration-cache")
            .withPluginClasspath()
            .build()

        val secondRun = GradleRunner.create()
            .withProjectDir(temporaryFolder.root)
            .withTestKitDir(testKitDir)
            .withArguments(generateJacocoTestKitProperties, "--configuration-cache")
            .withPluginClasspath()
            .build()

        assertThat(initialRun.task(":$generateJacocoTestKitProperties")?.outcome)
            .isEqualTo(TaskOutcome.SUCCESS)
        assertThat(initialRun.output)
            .contains("Calculating task graph as no configuration cache is available for tasks: $generateJacocoTestKitProperties")
        assertThat(secondRun.task(":$generateJacocoTestKitProperties")?.outcome)
            .isEqualTo(TaskOutcome.UP_TO_DATE)
        assertThat(secondRun.output)
            .contains("Reusing configuration cache.")
    }

    @Test
    fun `gradle properties file generated with custom destination file`() {
        temporaryFolder.newFile("build.gradle").fillFromResource("custom-destination.gradle")
        GradleRunner.create()
            .withProjectDir(temporaryFolder.root)
            .withTestKitDir(temporaryFolder.newFolder())
            .withArguments(generateJacocoTestKitProperties)
            .withPluginClasspath()
            .build()

        val args = readArgsFromProperties()
        assertThat(args)
            .endsWith("integration.exec\"")
    }

    @Test
    fun `gradle properties file generated for a custom task`() {
        temporaryFolder.newFile("build.gradle").fillFromResource("custom-task.gradle")
        GradleRunner.create()
            .withProjectDir(temporaryFolder.root)
            .withTestKitDir(temporaryFolder.newFolder())
            .withArguments("generateJacocoIntegrationTestKitProperties")
            .withPluginClasspath()
            .build()

        val args = readArgsFromProperties("integrationTest")
        assertThat(args)
            .endsWith("integrationTest.exec\"")
    }

    private fun readArgsFromProperties(taskName: String = "test"): String {
        val propertiesFile =
            File(temporaryFolder.root, "build/testkit/$taskName/testkit-gradle.properties")
        val properties = Properties()
        propertiesFile.inputStream().use { inputStream ->
            properties.load(inputStream)
        }
        return properties.getProperty("org.gradle.jvmargs")
    }

    private fun readLinesFromProperties(taskName: String = "test"): List<String> {
        val propertiesFile =
            File(temporaryFolder.root, "build/testkit/$taskName/testkit-gradle.properties")
        return propertiesFile.inputStream().use { inputStream ->
            inputStream.reader().readLines()
        }
    }
}
