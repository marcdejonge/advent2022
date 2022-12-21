plugins {
    kotlin("jvm") version "1.8.0-RC2"
}

repositories {
    mavenCentral()
}

dependencies {
    // Implementation dependencies
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.8.0-RC2"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // Testing libraries
    testImplementation(platform("org.junit:junit-bom:5.8.2"))
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
}

tasks {
    test {
        useJUnitPlatform()
    }

    register<Exec>("day") {
        dependsOn("compileKotlin", "processResources")
        group = "Execution"

        if (project.hasProperty("day") && project.property("day").toString().toIntOrNull() != null)
            environment["DAY"] = project.property("day")
        else
            throw GradleException("Missing property day, please add the -Pday=X parameter")

        if (project.hasProperty("file_postfix"))
            environment["FILE_POSTFIX"] = project.property("file_postfix")

        commandLine(
            "java",
            "-classpath",
            sourceSets.main.get().runtimeClasspath.asPath,
            "marcdejonge.advent2022.DaySolverKt"
        )
    }
}
