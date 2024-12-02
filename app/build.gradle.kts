plugins {
    id("com.android.application")
    kotlin("android")
    id("com.google.devtools.ksp")
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "io.github.stcksmsh.kap"
    compileSdk = 35

    defaultConfig {
        applicationId = "io.github.stcksmsh.kap"
        minSdk = 31
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }

    compileOptions {
        // Ensure consistency with Kotlin JVM target
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(11))
        }
    }
}

dependencies {
    implementation(libs.androidx.cardview)
    val composeVersion = "1.5.3"

    // Compose dependencies
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")
    implementation("androidx.compose.runtime:runtime:$composeVersion")
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.glance.material3)
    implementation(libs.androidx.work.runtime.ktx)

    implementation(libs.androidx.paging.compose)
    implementation(libs.androidx.paging.runtime)
    implementation(libs.paging.compose)
    // Vico chart library
    implementation(libs.vico.compose)
    implementation(libs.vico.compose.m3)
    implementation(libs.vico.core)
    implementation(libs.vico.views)

    // Debugging tools
    debugImplementation("androidx.compose.ui:ui-tooling:$composeVersion")

    // Room dependencies
    val roomVersion = "2.5.2" // Replace with the latest Room version
    implementation(libs.androidx.room.runtime)
    implementation("androidx.room:room-paging:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")
    ksp("androidx.room:room-paging:$roomVersion")

    // Optional - for Kotlin coroutines support
    implementation(libs.androidx.room.ktx)

    // Testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$composeVersion")
}
