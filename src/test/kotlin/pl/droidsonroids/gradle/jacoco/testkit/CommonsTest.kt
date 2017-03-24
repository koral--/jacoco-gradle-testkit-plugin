package pl.droidsonroids.gradle.jacoco.testkit

import org.assertj.core.api.Assertions.assertThat
import org.gradle.api.GradleException
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class CommonsTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun existingDirectoryExistenceEnsured() {
        val file = temporaryFolder.newFile()
        assertThat(file.parentFile).isDirectory()
        assertThat(file.ensureParentDirectoryExists()).isTrue()
    }

    @Test
    fun nonExistentDirectoryCreated() {
        val parent = temporaryFolder.newFolder()
        assertThat(parent.delete()).isTrue()
        val file = File(parent, "test")
        assertThat(file.parentFile).doesNotExist()
        assertThat(file.ensureParentDirectoryExists()).isTrue()
        assertThat(file.parentFile).isDirectory()
    }

    @Test(expected = GradleException::class)
    fun exceptionThrownIfParentDirectoryCouldNotBeCreated() {
        val file = temporaryFolder.newFile()
        assertThat(file).isFile()
        File(file, "test").ensureParentDirectoryExists()
    }

    @Test
    fun testKitDirectoryIsLocatedInBuildDirectory() {
        val project = ProjectBuilder.builder().build()
        assertThat(project.testKitDirectory()).hasParent(project.buildDir)
    }
}