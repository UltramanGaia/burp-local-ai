plugins {
    id("java")
}

repositories {
    mavenCentral()
    mavenLocal()
    maven { url = uri("https://jitpack.io") }

}

dependencies {
    compileOnly("net.portswigger.burp.extensions:montoya-api:2025.2")
    implementation("org.javassist:javassist:3.29.2-GA")
    implementation("cn.ultramangaia:gaiasec-java-jvmhelper:1.1.0")
    implementation("io.github.ollama4j:ollama4j:1.0.89")
}

tasks.withType<JavaCompile> {
    sourceCompatibility = "20"
    targetCompatibility = "20"
    options.encoding = "UTF-8"
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(configurations.runtimeClasspath.get().filter { it.isDirectory })
    from(configurations.runtimeClasspath.get().filterNot { it.isDirectory }.map { zipTree(it) })
    manifest {
        attributes(
            "Implementation-Version" to project.version,
            "Implementation-Vendor" to "GaiaSec",
            "Implementation-Vendor-Id" to "com.gaiasec",
        )
    }
}