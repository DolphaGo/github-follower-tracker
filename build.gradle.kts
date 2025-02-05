plugins {
    id("org.springframework.boot") version "3.4.2"
    id("io.spring.dependency-management") version "1.1.7"
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.1.10"
    id("org.jetbrains.kotlin.plugin.jpa") version "2.1.10"
    id("org.jetbrains.kotlin.plugin.spring") version "2.1.10"
    kotlin("kapt") version "2.1.10"
}

group = "dolphago"

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2024.0.0")
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-batch")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // Database
    implementation("com.mysql:mysql-connector-j")
    runtimeOnly("com.h2database:h2")
    implementation("com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.10.0")

    // logging
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.0")

    // kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.test {
    useJUnitPlatform()
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}