pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "PoposRoom"
include(":app")
include(":benchmark")
include(":core:data")
include(":core:domain")
include(":core:model")
include(":core:database")
include(":core:ui")
include(":core:common")
include(":core:designsystem")
include(":core:testing")
include(":feature:addonitem")
include(":feature:address")
include(":feature:cart")
include(":feature:cartorder")
include(":feature:category")
include(":feature:charges")
include(":feature:customer")
include(":feature:employee")
include(":feature:employee_payment")
include(":feature:employee_absent")
include(":feature:expenses")
include(":feature:home")
include(":feature:account")
include(":feature:order")
include(":feature:product")
include(":feature:print_order")
include(":feature:profile")
include(":feature:cart_selected")
include(":feature:settings")
include(":feature:printer_info")
