plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.david.fixittecnic"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.david.fixittecnic"
        minSdk = 24
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
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // --- NUESTRAS HERRAMIENTAS ---
    // Retrofit: Para conectarnos a tu API de Spring Boot
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    // Gson: Para traducir el texto de la base de datos a objetos Java
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // Glide: Para cargar las fotos del perfil del técnico o imágenes
    implementation("com.github.bumptech.glide:glide:4.16.0")
}