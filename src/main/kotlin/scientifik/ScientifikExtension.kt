package scientifik

import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.findByType

open class ScientifikExtension {
    var githubProject: String? = null
    var vcs: String? = null
    var bintrayRepo: String? = null
    var kdoc: Boolean = true
    var enableNative = false
}

internal val Project.scientifik: ScientifikExtension
    get() = extensions.findByType() ?: extensions.create("scientifik")