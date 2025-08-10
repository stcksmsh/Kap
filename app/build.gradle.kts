plugins {
    id("com.android.application")
    kotlin("android")
    // If you have a version-catalog alias for KSP, use: alias(libs.plugins.ksp)
    id("com.google.devtools.ksp") // ensure version is declared in the root plugins block or via catalog
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "io.github.stcksmsh.kap"
    compileSdk = 36

    defaultConfig {
        applicationId = "io.github.stcksmsh.kap"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    // Use Java 17 across the board
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }
}

dependencies {
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.activity.core)
    implementation(libs.material)

    // Compose app
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.runtime)
    implementation(libs.androidx.activity.compose)

    // Debug tooling (use app ui-tooling, not wear tooling)
    debugImplementation(libs.androidx.ui.tooling)

    // Work / Paging
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.paging.compose)
    implementation(libs.androidx.paging.runtime)

    // Room (KSP)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.paging)
    ksp(libs.androidx.room.compiler)

    // Health Connect
    implementation(libs.androidx.health.connect.client)

    // Wear
    implementation(libs.androidx.wear)
    implementation(libs.androidx.tiles)
    implementation(libs.play.services.wearable)

    // Glance
    implementation(libs.glance.appwidget)
    implementation(libs.androidx.glance.material3)

    // Charts
    implementation(libs.vico.compose)
    implementation(libs.vico.compose.m3)
    implementation(libs.vico.core)
    implementation(libs.vico.views)

    // CardView (if still used anywhere)
    implementation(libs.androidx.cardview)

    // Tests
    testImplementation(libs.junit)
}