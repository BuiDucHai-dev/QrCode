plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.ecomobile.qrcode"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.vtool.qrcodereader.barcodescanner"
        minSdk = 24
        targetSdk = 34
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
        dataBinding = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    sourceSets {
        getByName("main") {
            res.srcDirs(
                "src/main/res/f-home",
                "src/main/res/f-home_scan",
                "src/main/res/f-home_food",
                "src/main/res/f-home_history",
                "src/main/res/f-home_settings",
                "src/main/res/f-result",
            )
        }
    }
}

dependencies {
    implementation(project(":base"))
    implementation(project(":retrofit"))

    implementation(libs.room.ktx)
    kapt(libs.room.compiler)

    implementation(libs.barcode.scanning)
    implementation(libs.zxing.android)

    implementation(libs.open.graph.parser)

    implementation(libs.camera.core)
    implementation(libs.camera.camera2)
    implementation(libs.camera.view)
    implementation(libs.camera.lifecycle)
    implementation(libs.camera.extensions)
}