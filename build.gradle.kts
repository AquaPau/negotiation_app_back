plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.4.1"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "org.superapp"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Основные стартеры Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")

    //БД
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.postgresql:postgresql")
    implementation("com.h2database:h2")

    // Для работы с OAuth2
    implementation("org.springframework.security:spring-security-oauth2-client")
    implementation("org.springframework.security:spring-security-web")
    implementation("org.springframework.security:spring-security-config")

    //Swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")

    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")

    // import OpenAI API client BOM
    implementation(platform("com.aallam.openai:openai-client-bom:3.8.2"))
    implementation("com.aallam.openai:openai-client")
    runtimeOnly("io.ktor:ktor-client-okhttp")

    // Для работы с Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // Для тестирования
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")

    implementation("io.github.oshai:kotlin-logging-jvm:4.0.0")
    implementation("org.telegram:telegrambots-springboot-longpolling-starter:8.0.0")
    implementation("org.telegram:telegrambots-client:8.0.0")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:3.4.1") // Управление версиями зависимостей
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
