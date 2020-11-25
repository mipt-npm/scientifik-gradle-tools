package ru.mipt.npm.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * Create a separate target for node
 */
class KScienceNodePlugin : Plugin<Project> {
    override fun apply(target: Project) = target.run {
        //Apply multiplatform plugin is not applied, apply it
        if (plugins.findPlugin("org.jetbrains.kotlin.multiplatform") == null) {
            logger.info("Kotlin multiplatform plugin is not resolved. Adding it automatically")
            pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        }
        if (plugins.findPlugin(KScienceCommonPlugin::class) == null) {
            logger.info("KScience plugin is not resolved. Adding it automatically")
            pluginManager.apply(KScienceCommonPlugin::class)
        }

        configure<KotlinMultiplatformExtension> {
            js(name = "node", compiler = IR) {
                nodejs()
            }
            sourceSets {
                val commonMain by getting
                val nodeMain by creating {
                    dependsOn(commonMain)
                    dependencies{
                        api("org.jetbrains.kotlinx:kotlinx-nodejs:${KScienceVersions.kotlinxNodeVersion}")
                    }
                }

                val commonTest by getting

                val nodeTest by creating {
                    dependsOn(nodeMain)
                    dependsOn(commonTest)
                }
            }
        }

    }
}