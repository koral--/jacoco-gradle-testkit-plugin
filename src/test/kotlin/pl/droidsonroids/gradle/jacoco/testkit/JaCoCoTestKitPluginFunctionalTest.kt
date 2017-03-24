package pl.droidsonroids.gradle.jacoco.testkit

import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import pl.droidsonroids.gradle.jacoco.testkit.Tasks.generateJacocoTestKitProperties
import java.io.File

class JaCoCoTestKitPluginFunctionalTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun gradlePropertiesFileGenerated() {
        val text = """
plugins {
  id 'pl.droidsonroids.jacoco.testkit'
}
repositories {
  jcenter()
  mavenCentral()
}
"""
        temporaryFolder.newFile("build.gradle").writeText(text)
        GradleRunner.create()
                .withProjectDir(temporaryFolder.root)
                .withTestKitDir(temporaryFolder.newFolder())
                .withArguments(generateJacocoTestKitProperties)
                .withPluginClasspath()
                .build()

        val propertiesFile = File(temporaryFolder.root, "build/testkit/testkit-gradle.properties")
        assertThat(propertiesFile.readText()).startsWith("org.gradle.jvmargs:-javaagent:")
    }
}