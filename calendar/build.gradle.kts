
plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("android.extensions")
    id("org.jetbrains.dokka")
    id("maven-publish")
//    id("com.jfrog.bintray")
}

val group = project.property("group") as String
val version = project.property("version") as String

android {
    compileSdk = 31
    buildToolsVersion = "29.0.3"

    defaultConfig {
        minSdk = 23
        targetSdk = 31

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

}

dependencies {
//    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.7.10")
    implementation("androidx.recyclerview:recyclerview:1.3.0")
    testImplementation("junit:junit:4.13.2")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.6.0")
    implementation("androidx.appcompat:appcompat:1.5.1")
    implementation("androidx.core:core-ktx:1.8.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    //leak canary
//    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.7")
    api("com.jakewharton.threetenabp:threetenabp:1.2.1")
    implementation("com.auth0.android:jwtdecode:2.0.0")
}

tasks {
    val sourcesJar by creating(Jar::class) {
        archiveClassifier.set("sources")
        from(android.sourceSets.getByName("main").java.srcDirs)
    }

    val javadocJar by creating(Jar::class) {
        dependsOn.add(dokkaJavadoc)
        archiveClassifier.set("javadoc")
//        from(dokkaJavadoc.outputDirectory)
    }
}

artifacts {
    archives(tasks.getByName("sourcesJar"))
}
