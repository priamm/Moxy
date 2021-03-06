plugins {
    id("com.android.library")
    id("com.jfrog.bintray")
    id("maven-publish")
    id("moxy-publishing-plugin")
}

android {
    compileSdkVersion(29)

    defaultConfig {
        minSdkVersion(14)
        targetSdkVersion(29)

        consumerProguardFiles("../moxy/src/main/resources/META-INF/proguard/moxy.pro")
    }

    buildFeatures {
        buildConfig = false
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    api(project(":moxy"))
    compileOnly(Deps.android)

    compileOnly(project(":stub-androidx"))
    compileOnly(project(":stub-material"))
}

moxyPublishing {
    artifactName = "moxy-material"
    pomName = "Moxy Material"
    pomDescription = "Moxy Material library for Android"
}
