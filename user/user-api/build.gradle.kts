dependencies {
    implementation(project(":user:user-domain"))
    implementation(project(":user:user-infra"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
}

tasks.bootJar { enabled = false }

tasks.jar { enabled = true }
