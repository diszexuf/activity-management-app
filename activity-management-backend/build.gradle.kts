plugins {
    java
    id("org.springframework.boot") version "4.0.1"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.openapi.generator") version "7.18.0"
}

group = "com.github.diszexuf"
version = "0.0.1-SNAPSHOT"
description = "activity-management-backend"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.projectlombok:lombok")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")

    implementation("io.swagger.core.v3:swagger-annotations:2.2.20")
    implementation("org.openapitools:jackson-databind-nullable:0.2.6")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.postgresql:postgresql")
    implementation("org.springframework.boot:spring-boot-starter-liquibase")

    annotationProcessor("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    "developmentOnly"("org.springframework.boot:spring-boot-devtools")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

openApiGenerate {
    generatorName.set("spring")
    inputSpec.set("$rootDir/../openapi.yaml")
    outputDir.set("$projectDir/build/generated")

    configOptions.set(mapOf(
        "interfaceOnly" to "true",
        "useOptional" to "false",
        "skipDefaultInterface" to "true",
        "useTags" to "true",
        "useSpringBoot3" to "true",
        "library" to "spring-boot",
        "useJakartaEe" to "true",
        "serializableModel" to "true",
        "dateLibrary" to "java8",
        "useBeanValidation" to "true",
        "performBeanValidation" to "true"
    ))

    globalProperties.set(mapOf(
        "apis" to "",
        "models" to "",
        "supportingFiles" to "false"
    ))
}

tasks.register("cleanOpenApiGenerate", Delete::class) {
    delete("$projectDir/build/generated")
}

tasks.named("openApiGenerate") {
    dependsOn("cleanOpenApiGenerate")
}

tasks.named("compileJava") {
    dependsOn("openApiGenerate")
}

sourceSets {
    main {
        java {
            srcDir("$projectDir/build/generated/src/main/java")
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
