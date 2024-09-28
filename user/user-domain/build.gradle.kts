dependencies {
    compileOnly("org.springframework:spring-context")
    compileOnly("org.springframework:spring-tx")
}

tasks.bootJar { enabled = false }

tasks.jar { enabled = true }
