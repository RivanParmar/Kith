pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
    }
}

rootProject.name = "Kith"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":app")
include(":core:data")
include(":core:database")
include(":core:datastore")
include(":core:datastore-proto")
include(":core:designsystem")
include(":core:model")
include(":core:navigation")
include(":core:network")
include(":core:ui")

include(":feature:auth:api")
include(":feature:auth:impl")
include(":feature:browse:api")
include(":feature:browse:impl")
include(":feature:community:api")
include(":feature:home:api")
include(":feature:home:impl")
include(":feature:leaderboard:api")
include(":feature:leaderboard:impl")
include(":feature:onboarding:api")
include(":feature:onboarding:impl")
include(":feature:post:api")
include(":feature:post:impl")
include(":feature:profile:api")
include(":feature:profile:impl")

include(":sync:work")
