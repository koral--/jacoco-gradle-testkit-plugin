package pl.droidsonroids.gradle.jacoco.testkit

import org.assertj.core.api.Assertions.assertThat
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class CommonsTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun `test kit directory is located in build directory`() {
        val project = ProjectBuilder.builder().build()
        assertThat(project.testKitDir()).hasParent(File(project.buildDir, "testkit"))
    }
}
