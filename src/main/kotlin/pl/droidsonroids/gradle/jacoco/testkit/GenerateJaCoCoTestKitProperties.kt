package pl.droidsonroids.gradle.jacoco.testkit

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.WriteProperties
import java.io.File
import javax.inject.Inject

open class GenerateJaCoCoTestKitProperties @Inject constructor(
    objectFactory: ObjectFactory
) : WriteProperties() {
    @Input
    val destinationFile: Property<String> = objectFactory.property(String::class.java)

    @Input
    val jacocoRuntimePath: Property<String> = objectFactory.property(String::class.java)

    init {
        group = "verification"
        description = "Generates gradle.properties with JaCoCo java agent for TestKit"
        comment = "Generated by pl.droidsonroids.jacoco.testkit"

        super.property("org.gradle.jvmargs", {
            val destinationAsFile = File(destinationFile.get())
            "\"-javaagent:${jacocoRuntimePath.get()}=destfile=${destinationAsFile}\""
        })
    }
}
