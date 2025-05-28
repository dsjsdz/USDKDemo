import java.util.Properties

var keyProperties = Properties().apply {
    val propFile = file("key.properties")
    if (propFile.exists()) {
        propFile.inputStream().use { load(it) }
    }
}
plugins {
    id("com.android.application")
}

android {
    namespace = "cc.uling.usdk.demo"
    compileSdk = 34
    buildToolsVersion = "30.0.3"
    ndkVersion = "27.0.12077973"

    defaultConfig {
        applicationId = "cc.uling.usdk.demo"
        minSdk = 24
        @Suppress("OldTargetApi") // 保留你原有注释用途
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // config libs
    sourceSets {
        getByName("main") {
            jniLibs.srcDirs("libs")
        }
    }

    // signing configs
    signingConfigs {
        create("release") {
            keyAlias = keyProperties["keyAlias"] as String?
            keyPassword = keyProperties["keyPassword"] as String?
            storeFile = (keyProperties["storeFile"] as String?)?.let { file(it) }
            storePassword = keyProperties["storePassword"] as String?
        }
    }

    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
