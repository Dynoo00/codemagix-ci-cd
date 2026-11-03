import java.util.Properties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.application")
    id("kotlin-android")

    // The Flutter Gradle Plugin must be applied after Android and Kotlin plugins.
    id("dev.flutter.flutter-gradle-plugin")
    id("com.google.gms.google-services")
}

// Load local keystore properties.
val keystoreProperties = Properties()
val keystorePropertiesFile = rootProject.file("key.properties")

if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(keystorePropertiesFile.inputStream())
}

android {
    namespace = "com.example.codemagix"

    compileSdk = flutter.compileSdkVersion
    ndkVersion = flutter.ndkVersion

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    defaultConfig {
        applicationId = "com.example.codemagix"

        minSdk = flutter.minSdkVersion
        targetSdk = flutter.targetSdkVersion

        versionCode = flutter.versionCode
        versionName = flutter.versionName
    }

    signingConfigs {
        create("release") {

            val isCI = System.getenv("CI")?.toBoolean() == true

            if (isCI) {
                // Codemagic signing configuration.
                storeFile = file(
                    System.getenv("CM_KEYSTORE_PATH")
                        ?: error("CM_KEYSTORE_PATH is missing")
                )

                storePassword = System.getenv("CM_KEYSTORE_PASSWORD")
                    ?: error("CM_KEYSTORE_PASSWORD is missing")

                keyAlias = System.getenv("CM_KEY_ALIAS")
                    ?: error("CM_KEY_ALIAS is missing")

                keyPassword = System.getenv("CM_KEY_PASSWORD")
                    ?: error("CM_KEY_PASSWORD is missing")

            } else {
                // Local development signing configuration.
                keyAlias = keystoreProperties["keyAlias"]?.toString()

                keyPassword = keystoreProperties["keyPassword"]?.toString()

                storeFile = keystoreProperties["storeFile"]
                    ?.toString()
                    ?.let { file(it) }

                storePassword = keystoreProperties["storePassword"]?.toString()
            }
        }
    }

    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

flutter {
    source = "../.."
}

dependencies {
    // Import the Firebase BoM.
    implementation(platform("com.google.firebase:firebase-bom:35.1.0"))

    // Add Firebase dependencies here.
}