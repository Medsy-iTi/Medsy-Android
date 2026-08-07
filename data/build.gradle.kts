import java.util.Properties
import java.io.FileInputStream

import com.android.build.gradle.internal.tasks.AarMetadataReader.Companion.load

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.secrets)
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}
val baseUrl = localProperties.getProperty("BASE_URL") ?: "https://medsy-api-dev.com/"
val accessToken = localProperties.getProperty("ACCESS_TOKEN") ?: ""
val aiApiKey = localProperties.getProperty("AI_API_KEY") ?: ""

android {
    namespace = "com.medsy.data"
    compileSdk = 37

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
        buildConfigField("String", "ACCESS_TOKEN", "\"\"")
        buildConfigField("String", "AI_API_KEY", "\"$aiApiKey\"")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        buildConfig = true
    }

}

dependencies {
    implementation(project(":domain"))
    implementation(project(":designsystem"))
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)


    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.moshi)
    implementation(libs.moshi.kotlin)
    ksp(libs.moshi.codegen)


    // OkHttp
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    // Security
    implementation(libs.androidx.security.crypto)

    //Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

}

