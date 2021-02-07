import org.gradle.api.publish.maven.MavenPom
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinAndroidTarget
import ru.pocketbyte.kotlin.gradle.plugin.mpp_publish.*
import ru.pocketbyte.kotlin_mpp.plugin.publish.Publishing
import ru.pocketbyte.kotlin_mpp.plugin.publish.registerPlatformDependentPublishingTasks

plugins {
    id("com.android.library")
    id("kotlin-multiplatform")

    id("maven-publish")
    id("com.github.dcendents.android-maven")
    id("signing")
}

version = LibraryInfo.version
group = LibraryInfo.group
val name = "Kotlin Kydra Presentation"

android {
    compileSdkVersion(AndroidSdk.compile)
    buildToolsVersion(BuildVersion.androidTool)

    defaultConfig {
        minSdkVersion(AndroidSdk.min)
        targetSdkVersion(AndroidSdk.target)
        versionCode = 1
        versionName = project.version.toString()
    }
    buildTypes {
        getByName("release") {
            //
        }
        getByName("debug") {
            //
        }
    }
}

// =================================
// Common Source Sets
kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api("org.jetbrains.kotlin:kotlin-stdlib-common")
            }
        }

        val commonTest by getting {
            dependsOn(commonMain)
            dependencies {
                api("org.jetbrains.kotlin:kotlin-test")
                api("org.jetbrains.kotlin:kotlin-test-junit")
            }
        }
    }
}

// =================================
// JVM based targets
kotlin {
    android {
        publishLibraryVariants("release", "debug")
    }

    sourceSets {
        val commonMain by getting
        val commonTest by getting
    }
}

// =================================
// JS Target
kotlin {
    js()

    sourceSets {
        val jsMain by getting {
            dependencies {
                api("org.jetbrains.kotlin:kotlin-stdlib")
                api("org.jetbrains.kotlin:kotlin-stdlib-js")
            }
        }

        val jsTest by getting {
            dependsOn(jsMain)
            dependencies {
                api("org.jetbrains.kotlin:kotlin-test")
                api("org.jetbrains.kotlin:kotlin-test-js")
            }
        }
    }
}

// =================================
// Apple Targets
kotlin {
    macosX64() // macOS required
    iosX64()   // macOS required
    iosArm64() // macOS required

    watchosArm64() // macOS required
    watchosX86()   // macOS required

    tvosArm64() // macOS required
    tvosX64()   // macOS required

    sourceSets {
        val commonMain by getting
        val appleMain by creating {
            dependsOn(commonMain)
        }

        configure(listOf(
            getByName("macosX64Main"),
            getByName("iosX64Main"),
            getByName("iosArm64Main"),
            getByName("watchosX86Main"),
            getByName("watchosArm64Main"),
            getByName("tvosArm64Main"),
            getByName("tvosX64Main")
        )) {
            dependsOn(appleMain)
        }

        val appleTest by creating {
            dependsOn(appleMain)
        }

        configure(listOf(
            getByName("macosX64Test"),
            getByName("iosX64Test"),
            getByName("iosArm64Test"),
            getByName("watchosX86Test"),
            getByName("watchosArm64Test"),
            getByName("tvosArm64Test"),
            getByName("tvosX64Test")
        )) {
            dependsOn(appleTest)
        }
    }
}


//==================================================================================================
// Publication
//==================================================================================================

publishing {
    repositories {
        maven {
            name = "Bintray"
            url = uri("https://api.bintray.com/content/pocketbyte/kydra/${project.name}/${project.version}")
            credentials {
                username = project.findProperty("bintray.publish.user")?.toString() ?: ""
                password = project.findProperty("bintray.publish.apikey")?.toString() ?: ""
            }
        }
        maven {
            name = "Sonatype"
            url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2")
            credentials {
                username = project.findProperty("sonatype.publish.user")?.toString() ?: ""
                password = project.findProperty("sonatype.publish.password")?.toString() ?: ""
            }
        }
    }
}


fun configurePomDefault(pom: MavenPom, targetName: String?) {
    pom.apply {
        if (targetName != null) {
            name.set("Kotlin $name (${targetName})")
            description.set("$name implementation for target \'${targetName}\'")
        } else {
            name.set("Kotlin $name")
            description.set("Kotlin $name - Kotlin Multiplatform Library that helps to implement presentation layer.")
        }
        url.set("https://github.com/PocketByte/kotlin-kydra-presentation")
        issueManagement {
            url.set("https://github.com/PocketByte/kotlin-kydra-presentation/issues")
        }
        scm {
            url.set("https://github.com/PocketByte/kotlin-kydra-presentation.git")
        }
        developers {
            developer {
                organization.set("PocketByte")
                organizationUrl.set("pocketbyte.ru")
                email.set("mail@pocketbyte.ru")
            }
            developer {
                name.set("Denis Shurygin")
                email.set("sdi.linch@gmail.com")
            }
        }
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
    }
}

// Create Signing Tasks for Root
registerJarSigningTask(Publishing.ROOT_TARGET)
registerPomSigningTask(Publishing.ROOT_TARGET)
registerMetaSigningTask(Publishing.ROOT_TARGET)

// Create Signing Tasks for Targets
kotlin.targets.forEach { target ->
    when (target.platformType) {
        KotlinPlatformType.common, KotlinPlatformType.jvm, KotlinPlatformType.js -> {
            registerJarSigningTask(target.name)
            registerSourcesSigningTask(target.name)
            registerJavaDocSigningTask(target.name)
            registerPomSigningTask(target.name)
            registerMetaSigningTask(target.name)
        }
        KotlinPlatformType.native -> {
            registerKlibSigningTask(target.name,
                    target.compilations.getByName("main").output.allOutputs)
            registerSourcesSigningTask(target.name)
            registerJavaDocSigningTask(target.name)
            registerPomSigningTask(target.name)
            registerMetaSigningTask(target.name)

        }
        KotlinPlatformType.androidJvm -> {
            afterEvaluate {
                (target as? KotlinAndroidTarget)?.publishLibraryVariants?.forEach { variant ->
                    registerAarSigningTask(target.name, variant)
                    registerSourcesSigningTask(target.name, variant)
                    registerJavaDocSigningTask(target.name, variant)
                    registerPomSigningTask(target.name, variant)
                    registerMetaSigningTask(target.name, variant)
                }
            }
        }
    }
}

// Configure Root publication
publishing {
    publications {
        val kotlinMultiplatform by getting {
            (this as? MavenPublication)?.apply {
                addAllSigningsToPublication(this, Publishing.ROOT_TARGET)
                addJarSigningsToPublication(this, "metadata")
                configurePomDefault(pom, null)
            }
        }
    }
}

// Configure Target publications
kotlin {
    configure(listOf(
            metadata(), js(),
            macosX64(), iosX64(), iosArm64(),
            watchosArm64(), watchosX86(),
            tvosArm64(), tvosX64()
    )) {
        val targetName = name.upperFirstChar()
        mavenPublication {
            addAllSigningsToPublication(this, targetName)
            configurePomDefault(pom, targetName)
        }
    }
    android {
        val targetName = name.upperFirstChar()
        afterEvaluate {
            mavenPublication {
                val variant = if (this.artifactId.endsWith("debug")) // FIXME
                { "Debug"} else { "Release" }

                addAllSigningsToPublication(this, targetName, variant)
                configurePomDefault(pom, "$targetName $variant")
            }
        }
    }
}

registerPlatformDependentPublishingTasks()