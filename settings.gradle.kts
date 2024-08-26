rootProject.name = "openapioneof"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        // To get snapshot gradle plugin
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/") {
            mavenContent { snapshotsOnly() }
        }
    }
}