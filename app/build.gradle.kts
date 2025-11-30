plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.20"
}

import java.util.Properties
import java.io.FileInputStream
import java.io.FileOutputStream

// Auto-increment version code
val versionPropertiesFile = rootProject.file("version.properties")
val versionProperties = Properties()

if (versionPropertiesFile.exists()) {
    versionProperties.load(FileInputStream(versionPropertiesFile))
} else {
    versionProperties["VERSION_CODE"] = "1"
    versionProperties["VERSION_NAME"] = "1.0.0"
}

// Increment version code on every build
val currentVersionCode = (versionProperties["VERSION_CODE"] as String).toInt()
val newVersionCode = currentVersionCode + 1
versionProperties["VERSION_CODE"] = newVersionCode.toString()

// Update version name (patch number)
val versionNameParts = (versionProperties["VERSION_NAME"] as String).split(".")
val major = versionNameParts[0].toInt()
val minor = versionNameParts.getOrElse(1) { "0" }.toInt()
val patch = versionNameParts.getOrElse(2) { "0" }.toInt() + 1
versionProperties["VERSION_NAME"] = "$major.$minor.$patch"

// Save updated version properties
versionProperties.store(FileOutputStream(versionPropertiesFile), "Version properties")

android {
    namespace = "com.cashflow.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.cashflow.app"
        minSdk = 26
        targetSdk = 34
        versionCode = newVersionCode
        versionName = versionProperties["VERSION_NAME"] as String

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs = listOf(
            "-Xjvm-default=all",
            "-Xopt-in=kotlin.RequiresOptIn",
            "-Xopt-in=androidx.compose.material3.ExperimentalMaterial3Api"
        )
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.5"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    
    applicationVariants.all {
        val variant = this
        variant.outputs.all {
            val output = this as com.android.build.gradle.internal.api.BaseVariantOutputImpl
            val versionName = versionProperties["VERSION_NAME"] as String
            val versionCode = versionProperties["VERSION_CODE"] as String
            output.outputFileName = "cashflow-app-${variant.buildType.name}-v${versionName}-${versionCode}.apk"
        }
    }
}


dependencies {
    // Core Android
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.1")
    
    // Compose
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.5")
    
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")
    
    // Room
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    
    // Date/Time
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")
    
    // Serialization for export/import
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    
    // Work Manager for notifications
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    
    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.10.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

