[![AppVeyor](https://ci.appveyor.com/api/projects/status/pvd82vx2koufk4u5/branch/master?svg=true)](https://ci.appveyor.com/project/koral--/jacoco-gradle-testkit-plugin/branch/master)
[![Travis CI](https://travis-ci.org/koral--/jacoco-gradle-testkit-plugin.svg?branch=master)](https://travis-ci.org/koral--/jacoco-gradle-testkit-plugin)
[![codecov](https://codecov.io/gh/koral--/jacoco-gradle-testkit-plugin/branch/master/graph/badge.svg)](https://codecov.io/gh/koral--/jacoco-gradle-testkit-plugin)
[![Bitrise](https://www.bitrise.io/app/8be2125eb039c87e.svg?token=ZWi1ISNfiK0LCZ7Bk5g_TA&branch=master)](https://www.bitrise.io/app/8be2125eb039c87e)

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
  id "pl.droidsonroids.jacoco.testkit" version "1.0.3"
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

### Custom JaCoCo destination file
JaCoCo destination file path defaults to `${project.buildDir}/jacoco/test.exec` but may be changed
using `destinationFile` task property e.g.:
```groovy
task generateJacocoIntegrationTestKitProperties(type: GenerateJaCoCoTestKitProperties) {
  destinationFile = file('integration.exec')
}
```