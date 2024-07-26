import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.androidgitversion)
    id("maven-publish")
}

android {
    namespace = "com.wix.detoxbutler"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.wix.detoxbutler"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = androidGitVersion.code()
        versionName = androidGitVersion.name()

        archivesName = "detoxbutler-$versionName"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("aosp") {
            storeFile = file("keystore/platform.keystore")
            keyAlias = "platform"

            storePassword = "android"
            keyPassword = "android"
        }

        create("genymotion") {
            storeFile = file("keystore/genymotion_release.keystore")
            keyAlias = "platform"

            storePassword = "genymotion"
            keyPassword = "genymotion"
        }
    }

    flavorDimensions += "platform"
    productFlavors {
        create("aosp") {
            dimension = "platform"
            signingConfig = signingConfigs.getByName("aosp")
        }

        create("genymotion") {
            dimension = "platform"
            signingConfig = signingConfigs.getByName("genymotion")
        }
    }

    buildTypes {
        debug {
            signingConfig = getByName("release").signingConfig
        }
        release {
            isMinifyEnabled = true
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
}

afterEvaluate {
    publishing {
        publications {
            android.applicationVariants.forEach { variant ->
                if (variant.buildType.name == "release") {
                    create<MavenPublication>(variant.name) {
                        groupId = "com.wix.detoxbutler"
                        artifactId = "app-${variant.flavorName}"
                        version = androidGitVersion.name()

                        artifact(variant.outputs.first().outputFile) {
                            classifier = "release"
                            extension = "apk"
                        }
                    }
                }
            }
        }
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":api"))

    implementation(libs.timber)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
}