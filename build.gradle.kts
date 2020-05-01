import com.moowork.gradle.node.npm.NpmTask
import net.wasdev.wlp.gradle.plugins.extensions.FeatureExtension
import net.wasdev.wlp.gradle.plugins.extensions.ServerExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin on the JVM.
    kotlin("jvm") version "1.3.61"
    id("net.wasdev.wlp.gradle.plugins.Liberty") version "2.6.5"
    id("com.github.node-gradle.node") version "2.2.3"
    war
}

node {
    version = "12.16.2"
    npmVersion = "6.14.4"
    download = true
    workDir = file("$rootDir/src/main/frontend/node")
    nodeModulesDir = file("$rootDir/src/main/frontend")
}

tasks.register<Delete>("cleanClient") {
    delete(fileTree("$rootDir/src/main/webapp").matching {
        exclude("**/WEB-INF/**")
    })
}

tasks.register<Delete>("cleanNpm") {
    dependsOn("clean")
    delete("$rootDir/src/main/frontend/node", "$rootDir/src/main/frontend/node_modules")
}

tasks.register("npmUpdate") {
    dependsOn("npm_update")
}

tasks.register<NpmTask>("installDependencies") {
    dependsOn("npmSetup")
    setWorkingDir(File("$rootDir/src/main/frontend"))
    setArgs(listOf("install"))
}

tasks.register<NpmTask>("buildStandaloneClient") {
    dependsOn("npmInstall")

    inputs.files(fileTree("${rootDir}/src/main/frontend/").matching {
        exclude("**/dist")
    }).withPropertyName("sourceFiles")
    outputs.dir("${rootDir}/src/main/frontend/dist").withPropertyName("outputDir")

    setWorkingDir(File("$rootDir/src/main/frontend"))
    setArgs(listOf("run", "build"))
}

tasks.register<Copy>("copyFrontend") {
    dependsOn("cleanClient", "buildStandaloneClient")

    from(fileTree("$rootDir/src/main/frontend/build"))
    into("$rootDir/src/main/webapp")
}

repositories {
    // Use jcenter for resolving dependencies.
    // You can declare any Maven/Ivy/file repository here.
    mavenCentral()
}

dependencies {
    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.3.61")
    providedCompile("javax", "javaee-api", "6.0")

    libertyRuntime("com.ibm.websphere.appserver.runtime", "wlp-kernel", "20.0.0.3")

    // Use the Kotlin test library.
    testImplementation("org.jetbrains.kotlin:kotlin-test")

    // Use the Kotlin JUnit integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

val httpPort by extra { 8000 }
val httpsPort by extra { 8001 }
val jmsPort by extra { 18000 }
val jmsSslPort by extra { 18001 }

liberty {
    server = ServerExtension("jmsServer")
    server.dropins = listOf(tasks["war"])
    server.bootstrapProperties = mapOf("httpPort" to httpPort,
    "httpsPort" to httpsPort,
    "jmsPort" to jmsPort,
    "jmsSslPort" to jmsSslPort)
    server.configDirectory = file("src/main/liberty/config")
    server.isLooseApplication = false
    server.features = FeatureExtension()
    server.features.acceptLicense = true
}

tasks["clean"].dependsOn("libertyStop")
tasks["libertyStart"].dependsOn("installFeature")
tasks["npm_update"].dependsOn("libertyStop")
tasks["war"].dependsOn("copyFrontend")
//tasks["npmInstall"].outputs.upToDateWhen { false }
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}