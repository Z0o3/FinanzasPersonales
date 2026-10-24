plugins {
    java
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val junitVersion = "5.12.1"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(23)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

application {
    // Aplicación NO modular (sin module-info.java)
    mainClass.set("com.example.finanzas.Launcher")
}

javafx {
    version = "21.0.6"
    modules = listOf("javafx.controls", "javafx.fxml")
}

dependencies {
    // Firebase Realtime Database (API REST con java.net.http) + JSON
    implementation("com.google.code.gson:gson:2.11.0")

    // Exportación XLSX y PDF
    implementation("org.apache.poi:poi-ooxml:5.5.1")
    implementation("org.apache.poi:poi:5.5.1")
    implementation("com.itextpdf:itext-core:9.7.1")

    implementation("org.apache.logging.log4j:log4j-api:2.26.1")
    implementation("org.apache.logging.log4j:log4j-core:2.26.1")
    implementation("org.slf4j:slf4j-api:2.0.20")
    implementation("org.slf4j:slf4j-simple:2.0.20")

    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
