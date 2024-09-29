dependencies {
    /* Main Server Dependencies */
    implementation(project(":common-logging"))
    implementation(project(":common-exception"))
    implementation(project(":user:user-api"))

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-api:2.6.0")
    implementation("org.springframework.boot:spring-boot-starter-aop")

    /* Dependencies for damaba-api module */
//    implementation(project(":damaba:damaba-domain"))
//    implementation(project(":damaba:damaba-infra"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
}
