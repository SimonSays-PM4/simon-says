import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.2.3"
    id("io.spring.dependency-management") version "1.1.5"
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.spring") version "1.9.22"
    kotlin("plugin.noarg") version "1.7.10"
    kotlin("kapt") version "1.9.10"
    jacoco
    id("org.sonarqube") version "5.0.0.4638"
    id("io.gitlab.arturbosch.detekt") version "1.23.5"
}

group = "ch.zhaw.pm4"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

val versions = mapOf(
        "springDocOpenApi" to "2.4.0",
        "mapStruct" to "1.6.0.Beta1",
        "socketIoServer" to "4.1.2",
        "jacksonDatatypeJsonOrg" to "2.17.0",
        "springCloudContractWiremock" to "4.1.1",
        "springMockk" to "4.0.2",
        "testcontainers" to "1.19.7",
        "mysqlConnector" to "8.0.32"
)

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${versions["springDocOpenApi"]}")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.mapstruct:mapstruct:${versions["mapStruct"]}")
    implementation("io.socket:socket.io-server:${versions["socketIoServer"]}")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-json-org:${versions["jacksonDatatypeJsonOrg"]}")
    implementation("org.springframework.cloud:spring-cloud-contract-wiremock:${versions["springCloudContractWiremock"]}")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("com.ninja-squad:springmockk:${versions["springMockk"]}")
    testImplementation("org.testcontainers:junit-jupiter:${versions["testcontainers"]}")
    testImplementation("org.testcontainers:mysql:${versions["testcontainers"]}")
    runtimeOnly("com.mysql:mysql-connector-j:${versions["mysqlConnector"]}")
    kapt("org.mapstruct:mapstruct-processor:${versions["mapStruct"]}")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.test {
    // report is always generated after tests run
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    // tests are required to run before generating the report
    dependsOn(tasks.test)
    reports {
        xml.required = true
    }
}

jacoco {
    toolVersion = "0.8.11"
}

sonar{
    properties {
        property("sonar.organization","simonsays-pm4")
        property("sonar.projectKey","simonsays-backend")
        property("sonar.sources.",".")
        property("sonar.coverage.jacoco.xmlReportPaths","build/reports/jacoco/test/jacocoTestReport.xml")
        property("sonar.java.coveragePlugin","jacoco")
        property("sonar.junit.reportPaths","build/test-results/test")
        property("sonar.exclusions","**/test/**,**/build/**")
        property("sonar.host.url","https://sonarcloud.io")
    }

}

noArg {
    // Specify annotations to identify classes that need a no-arg constructor
    annotation("ch.zhaw.pm4.simonsays.entity.NoArgAnnotation")
}

detekt {
    buildUponDefaultConfig = true // preconfigure defaults
    allRules = false // activate all available (even unstable) rules.
    config = files("config/detekt/detekt.yml")
}

tasks.withType<Detekt>().configureEach {
    reports {
        html.required.set(true) // observe findings in your browser with structure and code snippets
        xml.required.set(true) // checkstyle like format mainly for integrations like Jenkins
        txt.required.set(true) // similar to the console output, contains issue signature to manually edit baseline files
        sarif.required.set(true) // standardized SARIF format (https://sarifweb.azurewebsites.net/) to support integrations with GitHub Code Scanning
        md.required.set(true) // simple Markdown format
    }
}

tasks.withType<Detekt>().configureEach {
    jvmTarget = "1.8"
}
tasks.withType<DetektCreateBaselineTask>().configureEach {
    jvmTarget = "1.8"
}
tasks.processResources {
    filesMatching("version.properties") {
        expand("version" to project.version)
    }
}