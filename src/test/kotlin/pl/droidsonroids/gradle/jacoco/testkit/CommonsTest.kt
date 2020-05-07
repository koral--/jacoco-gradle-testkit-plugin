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
    fun `existing parent existence ensured`() {
        val file = temporaryFolder.newFile()
        assertThat(file.parentFile).isDirectory()
        assertThat(file.ensureParentExists()).isTrue()
    }

    @Test
    fun `non existent parent created`() {
        val parent = temporaryFolder.newFolder()
        assertThat(parent.delete()).isTrue()
        val file = File(parent, "test")
        assertThat(file.parentFile).doesNotExist()
        assertThat(file.ensureParentExists()).isTrue()
        assertThat(file.parentFile).isDirectory()
    }

    @Test(expected = GradleException::class)
    fun `exception thrown if parent could not be created`() {
        val file = temporaryFolder.newFile()
        assertThat(file).isFile()
        File(file, "test").ensureParentExists()
    }

    @Test
    fun `test kit directory is located in build directory`() {
        val project = ProjectBuilder.builder().build()
        assertThat(project.testKitDir()).hasParent(File(project.buildDir, "testkit"))
    }
}