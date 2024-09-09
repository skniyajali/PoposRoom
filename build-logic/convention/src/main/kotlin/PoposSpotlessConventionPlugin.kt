import com.niyaj.samples.apps.popos.configureSpotless
import com.niyaj.samples.apps.popos.spotlessGradle
import org.gradle.api.Plugin
import org.gradle.api.Project

class PoposSpotlessConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            applyPlugins()

            spotlessGradle {
                configureSpotless(this)
            }
        }
    }

    private fun Project.applyPlugins() {
        pluginManager.apply {
            apply("com.diffplug.spotless")
        }
    }
}