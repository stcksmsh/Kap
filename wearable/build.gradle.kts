plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "io.github.stcksmsh.kap.wearable"
    compileSdk = 34

    defaultConfig {
        applicationId = "io.github.stcksmsh.kap.wearable"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

    }

    buildFeatures{
        compose = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.compose.foundation)

    // For Wear Material Design UX guidelines and specifications
    implementation(libs.compose.material)
    // For integration between Wear Compose and Androidx Navigation libraries
    implementation(libs.compose.navigation)

    // For Wear preview annotations
    implementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.runtime)
    implementation(libs.androidx.ui.graphics.android)
    implementation(libs.androidx.activity)

    implementation(libs.play.services.wearable)
}