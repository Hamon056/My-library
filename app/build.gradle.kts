plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)   // Compose-компилятор для Kotlin 2.0+
    alias(libs.plugins.ksp)              // Генератор реализации Room-запросов
}

android {
    namespace = "com.example.booklibrary"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.booklibrary"
        minSdk = 24          // Android 7.0
        targetSdk = 35       // Android 15
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            // Для учебного проекта обфускация не нужна
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
    }

    buildFeatures {
        compose = true       // Включаем Jetpack Compose
    }
}

dependencies {
    // Ядро AndroidX и жизненный цикл
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)

    // Jetpack Compose: BOM сам подбирает совместимые версии
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons)   // Полный набор иконок
    implementation(libs.androidx.navigation.compose)

    // Room — база данных поверх SQLite
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)                 // Поддержка корутин и Flow
    ksp(libs.androidx.room.compiler)                       // Аннотационный процессор

    // Корутины для асинхронной работы
    implementation(libs.kotlinx.coroutines.android)

    // Vico — столбчатый график прогресса чтения
    implementation(libs.vico.compose.m3)

    // Инструменты разработчика (только debug-сборка)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
