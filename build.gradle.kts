plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    kotlin("plugin.jpa") version "1.9.25"
    kotlin("kapt") version "1.7.10"
    id("org.springframework.boot") version "3.3.4"
    id("io.spring.dependency-management") version "1.1.6"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
    id("jacoco")
}

group = "com.damaba"
version = "0.0.1"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

extra["springCloudVersion"] = "2023.0.3"
val swaggerVersion = "2.6.0"
val jdslVersion = "3.5.4"
val jjwtVersion = "0.11.5"
val mapStructVersion = "1.5.2.Final"

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

dependencies {
    /**
     * Main Server Dependencies
     */
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
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$swaggerVersion")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-api:$swaggerVersion")

    /**
     * Infrastructure
     */
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("com.linecorp.kotlin-jdsl:jpql-dsl:$jdslVersion")
    implementation("com.linecorp.kotlin-jdsl:jpql-render:$jdslVersion")
    implementation("com.linecorp.kotlin-jdsl:spring-data-jpa-support:$jdslVersion")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")

    // RDB
    implementation("com.h2database:h2")
    implementation("com.mysql:mysql-connector-j")

    // JWT
    implementation("io.jsonwebtoken:jjwt-api:$jjwtVersion")
    implementation("io.jsonwebtoken:jjwt-impl:$jjwtVersion")
    implementation("io.jsonwebtoken:jjwt-jackson:$jjwtVersion")

    // AWS S3
    implementation(platform("software.amazon.awssdk:bom:2.27.21"))
    implementation("software.amazon.awssdk:s3")

    /**
     * Common
     */
    // MapStruct
    implementation("org.mapstruct:mapstruct:$mapStructVersion")
    annotationProcessor("org.mapstruct:mapstruct-processor:$mapStructVersion")
    kapt("org.mapstruct:mapstruct-processor:$mapStructVersion")
    kaptTest("org.mapstruct:mapstruct-processor:$mapStructVersion")

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
                        "**/adapter/inbound/**/*Controller*",
                        "**/adapter/outbound/**/*Repository*",
                        "**/adapter/outbound/**/*Adapter*",
                        "**/application/listener/**/*EventListener*",
                        "**/application/service/**",
                        "**/application/port/**",
                        "**/domain/**",
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
                "*.adapter.inbound.*.*Controller*",
                "*.adapter.outbound.*.*Repository*",
                "*.adapter.outbound.*.*Adapter*",
                "*.application.listener.*.*EventListener*",
                "*.application.service.*",
                "*.application.port.*",
                "*.domain.*.*",
            )

            excludes = listOf(
                "*.domain.file.*",
                "*.domain.exception.*",
            )
        }
    }
}
