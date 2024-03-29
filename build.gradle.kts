//plugins {
//    kotlin("jvm").version("1.4.31")
//}

buildscript {
//    val kotlin_version by extra("1.3.72")
    repositories {
        google()
        jcenter()

    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.0.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.72")
        classpath("com.jfrog.bintray.gradle:gradle-bintray-plugin:1.8.5")
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:0.10.1")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
    }
}
