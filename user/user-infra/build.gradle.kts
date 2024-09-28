plugins {
    kotlin("plugin.jpa") version "1.9.25"
}

dependencies {
    implementation(project(":user:user-domain"))

    // JPA
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // RDB
    implementation("com.h2database:h2")
    implementation("com.mysql:mysql-connector-j")
}

tasks.bootJar { enabled = false }

tasks.jar { enabled = true }
