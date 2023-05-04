[![AppVeyor](https://ci.appveyor.com/api/projects/status/pvd82vx2koufk4u5/branch/master?svg=true)](https://ci.appveyor.com/project/koral--/jacoco-gradle-testkit-plugin/branch/master)
[![codecov](https://codecov.io/gh/koral--/jacoco-gradle-testkit-plugin/branch/master/graph/badge.svg)](https://codecov.io/gh/koral--/jacoco-gradle-testkit-plugin)
[![Build Status](https://app.bitrise.io/app/8be2125eb039c87e/status.svg?token=ZWi1ISNfiK0LCZ7Bk5g_TA&branch=master)](https://app.bitrise.io/app/8be2125eb039c87e)

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
  id "pl.droidsonroids.jacoco.testkit" version "1.0.12"
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

### Adding coverage for another task (ex. integrationTest)
By default the plugin configures the `test` task for any project with Java plugin applied.

To configure the coverage for another task just add something like this:
```groovy
jacocoTestKit {
    applyTo("intTestRuntimeOnly", tasks.named("integrationTest"))
}
```

### Custom JaCoCo destination file
JaCoCo destination file path reads it from the `JacocoTaskExtension` so you can change it like this:
```groovy
tasks.named("test").configure {
    jacoco {
        destinationFile = file('integration.exec')
    }
}
```

### Requirements
Minimum supported versions:
- Gradle: **7.6**
- Java: **1.8**

### Backwards compatibility

#### Migrating from 1.0.9 or older

Starting from version 1.0.10 the legacy plugin coordinates have changed. For example:

```
classpath("gradle.plugin.pl.droidsonroids.gradle.jacoco:jacoco-gradle-testkit-plugin:1.0.9")
```

became:

```
classpath("pl.droidsonroids.gradle.jacoco:pl.droidsonroids.gradle.jacoco:1.0.10")
```

Note the `gradle.plugin` prefix has gone. This change does NOT affect the plugins DSL:

```
plugins {
  id("pl.droidsonroids.jacoco.testkit") version "1.0.10"
}
```

