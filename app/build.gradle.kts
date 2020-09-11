
plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("android.extensions")
    id("org.jetbrains.dokka")
    id("maven-publish")
    id("com.jfrog.bintray")
}

val group = project.property("group") as String
val version = project.property("version") as String

android {
    compileSdkVersion(29)
    buildToolsVersion = "29.0.2"

    defaultConfig {
        minSdkVersion(23)
        targetSdkVersion(29)
        versionCode = 1
        versionName = version

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.72")
    testImplementation("junit:junit:4.13")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("androidx.core:core-ktx:1.3.1")
    androidTestImplementation("androidx.test.ext:junit:1.1.1")
    implementation("androidx.constraintlayout:constraintlayout:1.1.3")
    //leak canary
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.2.0")

    implementation("com.auth0.android:jwtdecode:2.0.0")
}

tasks {
    val dokka by getting(org.jetbrains.dokka.gradle.DokkaTask::class) {
        outputFormat = "html"
        outputDirectory = "$projectDir/../docs/"
    }
}

apply(from = rootProject.file("gradle/publish.gradle"))

tasks {
    val dokkaJavadoc by creating(org.jetbrains.dokka.gradle.DokkaTask::class) {
        outputFormat = "javadoc"
        outputDirectory = "$buildDir/javadoc"
    }

    val sourcesJar by creating(Jar::class) {
        archiveClassifier.set("sources")
        from(android.sourceSets.getByName("main").java.srcDirs)
    }

    val javadocJar by creating(Jar::class) {
        dependsOn.add(dokkaJavadoc)
        archiveClassifier.set("javadoc")
        from(dokkaJavadoc.outputDirectory)
    }
}

artifacts {
    archives(tasks.getByName("sourcesJar"))
}