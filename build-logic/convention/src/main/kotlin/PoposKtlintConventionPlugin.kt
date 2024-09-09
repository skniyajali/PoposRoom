import org.gradle.api.Plugin
import org.gradle.api.Project

class PoposKtlintConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            applyPlugins()
        }
    }

    private fun Project.applyPlugins() {
        pluginManager.apply {
            apply("org.jlleitschuh.gradle.ktlint")
        }
    }
}