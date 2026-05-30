import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.detekt)
}

android {
    namespace = "com.emotionfriend"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.emotionfriend"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Load .env file and inject as buildConfigField
        val envFile = rootProject.file(".env")
        val openAiKey: String = if (envFile.exists()) {
            val props = Properties().apply { load(FileInputStream(envFile)) }
            props.getProperty("OPENAI_API_KEY") ?: ""
        } else ""
        buildConfigField("String", "OPENAI_API_KEY", '"' + openAiKey + '"')
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Signing config populated from env vars set by CI; ignored in local dev.
            val storeFile = System.getenv("SIGNING_STORE_FILE")
            val storePass = System.getenv("SIGNING_STORE_PASSWORD")
            val keyAlias  = System.getenv("SIGNING_KEY_ALIAS")
            val keyPass   = System.getenv("SIGNING_KEY_PASSWORD")
            if (storeFile != null && storePass != null && keyAlias != null && keyPass != null) {
                signingConfig = signingConfigs.create("release").apply {
                    this.storeFile = file(storeFile)
                    this.storePassword = storePass
                    this.keyAlias = keyAlias
                    this.keyPassword = keyPass
                }
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    // Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)

    // Compose BOM
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Material Icons Extended (for VolumeUp)
    implementation(libs.androidx.material.icons.extended)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    // CameraX
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)

    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Ktor HTTP client
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.kotlinx.serialization.json)

    // Debug
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
}

detekt {
    // Point to the shared config file at the android-app root.
    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
    // Build DOES NOT fail on detekt issues in this project (student/demo mode).
    // Flip to true when the team is ready to enforce rules strictly.
    ignoreFailures = true
    // Include generated sources produced by KSP/Hilt? No — only analyse hand-written code.
    source.setFrom(files("src/main/java", "src/main/kotlin"))
}
