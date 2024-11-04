plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "io.github.stcksmsh.kap"
    compileSdk = 34

    defaultConfig {
        applicationId = "io.github.stcksmsh.kap" // Set to match your namespace
        minSdk = 31
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    val composeVersion = "1.5.3" // Ensure compatibility with all Compose dependencies

    implementation("androidx.compose.material3:material3:1.3.1") // Updated to use composeVersion
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")
//    implementation("androidx.compose.runtime:runtime-livedata:$composeVersion")

    // For activities with Compose support
    implementation("androidx.activity:activity-compose:$composeVersion")

    // For testing
    testImplementation("junit:junit:4.13.2")

    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$composeVersion")
    debugImplementation("androidx.compose.ui:ui-tooling:$composeVersion")
}
