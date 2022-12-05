plugins {
    kotlin("jvm") version "1.7.21"
}

repositories {
    mavenCentral()
}

dependencies {
    // Implementation dependencies
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.7.21"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.diogonunes:JColor:5.5.1")

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
        dependsOn("build")
        group = "Execution"
        environment["DAY"] = if(project.hasProperty("day")) project.property("day") else "1"
        environment["BIG_POSTFIX"] = if (project.hasProperty("big_postfix")) project.property("big_postfix") else ""
        commandLine(
            "java",
            "-classpath",
            sourceSets.main.get().runtimeClasspath.asPath,
            "marcdejonge.advent2022.DaySolverKt"
        )
    }
}
