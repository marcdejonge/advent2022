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
}
