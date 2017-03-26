# jacoco-gradle-testkit-plugin
Gradle plugin for [JaCoCo](http://www.eclemma.org/jacoco/) code coverage
in tests using [Gradle TestKit](https://docs.gradle.org/current/userguide/test_kit.html).

## Motivation
There is no built-it support for code coverage in TestKit. Those tests run
in separate JVM and configuration of [JaCoCo plugin](https://docs.gradle.org/current/userguide/jacoco_plugin.html)
is not taken into account. See [Gradle forum post](https://discuss.gradle.org/t/gradle-plugins-integration-tests-code-coverage-with-jacoco-plugin/12403)
for more details.

## Usage
- Apply plugin in `build.gradle`:
```groovy
plugins {
  id "pl.droidsonroids.jacoco.testkit" version "1.0.0"
}
```
This will add `testkit-gradle.properties` system resource.

- Create `gradle.properties` file used by `GradleRunner` and populate it
with content from mentioned resource.
Sample kotlin code:
```kotlin
class AwesomeTest {

    fun InputStream.toFile(file: File) {
        use { input ->
            file.outputStream().use { input.copyTo(it) }
        }
    }

    fun GradleRunner.withJaCoCo(): GradleRunner {
        javaClass.classLoader.getResourceAsStream("testkit-gradle.properties").toFile(File(projectDir, "gradle.properties"))
        return this
    }

    @get:Rule
    val temporaryFolder = TemporaryProjectFolder()

    @Test
    fun `empty project builds successfuly`() {
        val result = GradleRunner.create()
                .withProjectDir(temporaryFolder.root)
                .withTestKitDir(temporaryFolder.newFolder())
                .withPluginClasspath()
                .withJaCoCo()
                .build()
    }
}
```