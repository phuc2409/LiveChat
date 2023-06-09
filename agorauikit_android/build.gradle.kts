plugins {
    id("maven-publish")
//    id("maven")
    id("com.android.library")
    id("kotlin-android")
    id("org.jetbrains.dokka")
    id("org.jlleitschuh.gradle.ktlint")
    kotlin("plugin.serialization") version "1.4.10"
}
// group = "com.github.agoraio-community"

android {
    namespace = "io.agora.agorauikit_android"
    compileSdk = 33
    buildToolsVersion = "33.0.2"

    defaultConfig {
        minSdk = 24
        targetSdk = 30

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles("proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

// Because the components are created only during the afterEvaluate phase, you must
// configure your publications using the afterEvaluate() lifecycle method.
afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                // Applies the component for the release build variant.
                from(components["release"])
                groupId = "com.github.agoraio-community"
                artifactId = "final"
                version = "2.0.6"
            }
            // Creates a Maven publication called “debug”.
            create<MavenPublication>("debug") {
                // Applies the component for the debug build variant.
                from(components["debug"])
                groupId = "com.github.agoraio-community"
                artifactId = "final-debug"
                version = "2.0.6"
            }
        }
    }
}

dependencies {
    implementation("com.google.android.material:material:1.9.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.20")
    implementation("androidx.core:core-ktx:1.10.1")
    api("io.agora.rtc:full-sdk:4.1.1")
    api("io.agora.rtm:rtm-sdk:1.5.3")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

tasks.dokkaHtml.configure {
    suppressInheritedMembers.set(true)
}
