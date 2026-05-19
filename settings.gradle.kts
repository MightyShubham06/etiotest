pluginManagement {

    repositories {

        google()

        mavenCentral()

        gradlePluginPortal()

        maven(
            "https://maven.juspay.in/jp-build-packages/hyper-sdk/"
        )
    }
}

dependencyResolutionManagement {

    repositoriesMode.set(
        RepositoriesMode.FAIL_ON_PROJECT_REPOS
    )

    repositories {

        google()

        mavenCentral()

        maven(
            "https://maven.juspay.in/jp-build-packages/hyper-sdk/"
        )
    }
}

rootProject.name = "ETIOTEST"

include(":app")