import com.niyaj.samples.apps.popos.configureDetekt
import com.niyaj.samples.apps.popos.detektGradle
import org.gradle.api.Plugin
import org.gradle.api.Project

class PoposDetektConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            applyPlugins()

            detektGradle {
                configureDetekt(this)
            }
        }
    }

    private fun Project.applyPlugins() {
        pluginManager.apply {
            apply("io.gitlab.arturbosch.detekt")
        }
    }
}