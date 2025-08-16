plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
    alias(libs.plugins.androidx.navigation.safeargs)
}

android {
    namespace = "com.example.firebasechatapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.firebasechatapp"
        minSdk = 33
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Firebase
    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:34.1.0"))

    // Add the dependency for the Firebase Authentication library
    implementation("com.google.firebase:firebase-auth")

    // UI Components, States + StateHolders
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // Navigation
    implementation(libs.androidx.navigation.ui.ktx)

    // DI
    implementation(libs.koin.android)

    // Networking
    implementation(libs.retrofit.moshi)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.retrofit.coroutines.adapter)
    implementation(libs.gson)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.okhttp)

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.androidx.annotation)

    // Debugging
    debugImplementation(libs.chucker.debug)
    releaseImplementation(libs.chucker.release)

    // Permissions
    implementation(libs.easy.permissions)

    // Swipe to refresh
    implementation(libs.androidx.swiperefreshlayout)

}