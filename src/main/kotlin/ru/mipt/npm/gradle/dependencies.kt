package ru.mipt.npm.gradle

import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

enum class DependencyConfiguration {
    API,
    IMPLEMENTATION,
    COMPILE_ONLY
}

enum class DependencySourceSet(val setName: String, val suffix: String) {
    MAIN("main", "Main"),
    TEST("test", "Test")
}

internal fun Project.useDependency(
    vararg pairs: Pair<String, String>,
    dependencySourceSet: DependencySourceSet = DependencySourceSet.MAIN,
    dependencyConfiguration: DependencyConfiguration = DependencyConfiguration.IMPLEMENTATION
) {
    pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
        configure<KotlinMultiplatformExtension> {
            sourceSets {
                pairs.forEach { (target, dep) ->
                    val name = target + dependencySourceSet.suffix
                    findByName(name)?.apply {
                        dependencies {
                            when (dependencyConfiguration) {
                                DependencyConfiguration.API -> api(dep)
                                DependencyConfiguration.IMPLEMENTATION -> implementation(dep)
                                DependencyConfiguration.COMPILE_ONLY -> compileOnly(dep)
                            }
                        }
                    }
                }
            }


            pairs.find { it.first == "jvm" }?.let { dep ->
                pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
                    sourceSets.findByName(dependencySourceSet.setName)?.apply {
                        dependencies.apply {
                            val configurationName = when (dependencyConfiguration) {
                                DependencyConfiguration.API -> apiConfigurationName
                                DependencyConfiguration.IMPLEMENTATION -> implementationConfigurationName
                                DependencyConfiguration.COMPILE_ONLY -> compileOnlyConfigurationName
                            }
                            add(configurationName, dep.second)
                        }
                    }
                }
            }

            pairs.find { it.first == "js" }?.let { dep ->
                pluginManager.withPlugin("org.jetbrains.kotlin.js") {
                    sourceSets.findByName(dependencySourceSet.setName)?.apply {
                        dependencies.apply {
                            val configurationName = when (dependencyConfiguration) {
                                DependencyConfiguration.API -> apiConfigurationName
                                DependencyConfiguration.IMPLEMENTATION -> implementationConfigurationName
                                DependencyConfiguration.COMPILE_ONLY -> compileOnlyConfigurationName
                            }
                            add(configurationName, dep.second)
                        }
                    }
                }
            }
        }

    }
}

internal fun Project.useCommonDependency(
    dep: String,
    dependencySourceSet: DependencySourceSet = DependencySourceSet.MAIN,
    dependencyConfiguration: DependencyConfiguration = DependencyConfiguration.IMPLEMENTATION
): Unit = pluginManager.run {
    withPlugin("org.jetbrains.kotlin.multiplatform") {
        configure<KotlinMultiplatformExtension> {
            sourceSets.findByName("common${dependencySourceSet.suffix}")?.apply {
                dependencies {
                    when (dependencyConfiguration) {
                        DependencyConfiguration.API -> api(dep)
                        DependencyConfiguration.IMPLEMENTATION -> implementation(dep)
                        DependencyConfiguration.COMPILE_ONLY -> compileOnly(dep)
                    }
                }
            }

            withPlugin("org.jetbrains.kotlin.jvm") {
                sourceSets.findByName(dependencySourceSet.setName)?.apply {
                    dependencies.apply {
                        val configurationName = when (dependencyConfiguration) {
                            DependencyConfiguration.API -> apiConfigurationName
                            DependencyConfiguration.IMPLEMENTATION -> implementationConfigurationName
                            DependencyConfiguration.COMPILE_ONLY -> compileOnlyConfigurationName
                        }
                        add(configurationName, dep)
                    }
                }
            }
            withPlugin("org.jetbrains.kotlin.js") {
                sourceSets.findByName(dependencySourceSet.setName)?.apply {
                    dependencies.apply {
                        val configurationName = when (dependencyConfiguration) {
                            DependencyConfiguration.API -> apiConfigurationName
                            DependencyConfiguration.IMPLEMENTATION -> implementationConfigurationName
                            DependencyConfiguration.COMPILE_ONLY -> compileOnlyConfigurationName
                        }
                        add(configurationName, dep)
                    }
                }
            }
        }
    }
}