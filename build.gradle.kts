import groovy.xml.dom.DOMCategory.attributes

plugins {
    id("java")
}

group = "ru.megboyzz"
version = "1.0-SNAPSHOT"


tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "Main"
    }
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}