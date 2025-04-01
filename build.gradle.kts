plugins {
    kotlin("jvm") version "2.1.10"
    id("org.openapi.generator") version "7.0.1"
}

group = "com.mms.proco"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.moshi:moshi:1.15.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.0")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb:2.3.0.RELEASE")
    implementation("org.mongodb:mongodb-driver-kotlin-coroutine:4.10.1")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(23)
}

openApiGenerate{
    inputSpec.set("$rootDir/openapi/todo.api.yaml")
    generatorName.set("kotlin")
}

sourceSets {
    main {
        java {
            srcDir("${buildDir}/generate-resources/main/src")
        }
    }
}

//tasks.compileKotlin {
//    dependsOn("openAPIGenerate")
//}