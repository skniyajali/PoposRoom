
package com.niyaj.samples.apps.popos

/**
 * This is shared between :app and :benchmarks module to provide configurations type safety.
 */
@Suppress("unused")
enum class PoposBuildType(val applicationIdSuffix: String? = null) {
    DEBUG(".debug"),
    RELEASE
}
