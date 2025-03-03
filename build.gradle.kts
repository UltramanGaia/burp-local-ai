plugins {
    id("java")
}

repositories {
    maven {
        setUrl("https://maven.aliyun.com/repository/public/")
    }
    mavenLocal()
    mavenCentral()
    maven { url = uri("https://jitpack.io") }

}

dependencies {
    compileOnly("net.portswigger.burp.extensions:montoya-api:2025.2")
    implementation("org.javassist:javassist:3.29.2-GA")
    implementation("cn.ultramangaia:gaiasec-java-jvmhelper:1.1.0")
    implementation("org.swinglabs:swingx:1.6.1")
    implementation("io.github.ollama4j:ollama4j:1.0.89")
    implementation("com.alibaba.fastjson2:fastjson2:2.0.56")
    implementation("com.github.CoreyD97:Burp-Montoya-Utilities:v1.0.0")
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