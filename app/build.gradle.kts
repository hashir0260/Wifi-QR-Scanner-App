plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

android {
    namespace = "com.wifi.wifiqrcodepasswordscanner"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.wifi.wifiqrcodepasswordscanner_test"
        minSdk = 23
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }


    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

}

dependencies {

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    implementation("com.intuit.sdp:sdp-android:1.1.1")
    implementation ("com.nabinbhandari.android:permissions:3.8")

    implementation ("com.github.joielechong:countrycodepicker:2.4.2")
    implementation("com.github.blikoon:QRCodeScanner:0.1.2")
    implementation("me.dm7.barcodescanner:zxing:1.9")


    implementation ("androidx.room:room-runtime:2.6.1")
    kapt ("androidx.room:room-compiler:2.6.1")
    implementation ("androidx.room:room-ktx:2.6.1")
    testImplementation ("androidx.room:room-testing:2.6.1")

    implementation ("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation ("com.google.zxing:core:3.4.1")

    implementation ("androidx.fragment:fragment-ktx:1.8.2")
    implementation ("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation ("androidx.navigation:navigation-ui-ktx:2.7.7")

    implementation ("androidx.cardview:cardview:1.0.0")

    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")
}