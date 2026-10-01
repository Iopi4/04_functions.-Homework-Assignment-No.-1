plugins {
    kotlin("jvm") version "2.2.20"
    jacoco
}

group = "ru.netology"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.2.20")
    testImplementation("junit:junit:4.13.2")
}

tasks.test {
    useJUnit()
}



kotlin {
    jvmToolchain(21)
}