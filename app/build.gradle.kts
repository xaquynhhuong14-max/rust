plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.filemanager"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.filemanager"
        minSdk = 23
        targetSdk = 32
        versionCode = 1
        versionName = "1.0"
    }

    sourceSets["main"].jniLibs.srcDir("src/main/jniLibs")
}

dependencies {
    implementation("androidx.documentfile:documentfile:1.0.1")
}

// Biên dịch thư viện Rust thành libfilecore.so cho armeabi-v7a trước khi build APK.
val buildRust by tasks.registering(Exec::class) {
    workingDir = rootProject.projectDir
    commandLine(
        "cargo", "ndk",
        "-t", "armeabi-v7a",
        "-o", "app/src/main/jniLibs",
        "build", "--release",
        "--manifest-path", "rust/Cargo.toml"
    )
    inputs.dir(rootProject.file("rust/src"))
    inputs.file(rootProject.file("rust/Cargo.toml"))
    outputs.dir(file("src/main/jniLibs/armeabi-v7a"))
}

tasks.named("preBuild").configure { dependsOn(buildRust) }
