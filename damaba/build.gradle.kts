plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    kotlin("plugin.jpa") version "1.9.25"
    id("org.springframework.boot") version "3.3.4"
    id("io.spring.dependency-management") version "1.1.6"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
    id("jacoco")
}

group = "com.damaba.damaba"
version = "0.0.1"

repositories { mavenCentral() }

java { toolchain { languageVersion = JavaLanguageVersion.of(17) } }

extra["springCloudVersion"] = "2023.0.3"

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

dependencies {
    /**
     * Main Server Dependencies
     */
    implementation(project(":user"))
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-aop")

    /**
     * Controller(API)
     */
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Spring Security
    implementation("org.springframework.boot:spring-boot-starter-security")
    testImplementation("org.springframework.security:spring-security-test")

    // Swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-api:2.6.0")

    /**
     * Infrastructure
     */
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")

    // RDB
    implementation("com.h2database:h2")
    implementation("com.mysql:mysql-connector-j")

    // AWS S3
    implementation(platform("software.amazon.awssdk:bom:2.27.21"))
    implementation("software.amazon.awssdk:s3")

    /**
     * Common
     */
    implementation(project(":common-logging"))
    implementation(project(":common-exception"))

    // Test container
    testImplementation("org.testcontainers:testcontainers:1.20.2")
    testImplementation("org.testcontainers:junit-jupiter:1.20.2")

    implementation("org.springframework.cloud:spring-cloud-starter-loadbalancer")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("io.mockk:mockk:1.13.13")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

ktlint {
    android.set(false)
    outputToConsole.set(true)
    ignoreFailures.set(false)
    filter { exclude("**/generated/**") }
}

jacoco {
    toolVersion = "0.8.12"
}

tasks.test {
    finalizedBy(tasks.ktlintCheck)
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required = false
        csv.required = false
        html.required = true
        classDirectories.setFrom(
            sourceSets.main.get().output.asFileTree.matching {
                include(
                    listOf(
                        "**/controller/**/*Controller*",
                        "**/application/**/*UseCase*",
                        "**/domain/**/*",
                        "**/infrastructure/**/*Repository*",
                        "**/infrastructure/**/*Service*",
                        "**/infrastructure/**/*EventListener*",
                    ),
                )
            },
        )
    }
    finalizedBy(tasks.jacocoTestCoverageVerification)
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            isEnabled = true
            element = "CLASS"

            limit {
                counter = "BRANCH"
                value = "COVEREDRATIO"
                minimum = 0.9.toBigDecimal()
            }

            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = 0.9.toBigDecimal()
            }

            includes = listOf(
                "*.controller.*.*Controller*",
                "*.application.*.*UseCase*",
                "*.domain.*.*",
                "*.infrastructure.*.*Repository*",
                "*.infrastructure.*.*Service*",
                "*.infrastructure.*.*EventListener*",
            )
        }
    }
}
