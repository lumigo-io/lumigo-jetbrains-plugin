rootProject.name = "lumigo-intellij-plugin"
include("java")

plugins {
    id("org.danilopianini.gradle-pre-commit-git-hooks") version "1.0.4"
}

gitHooks {
    // Configuration
    commitMsg { conventionalCommits() } // Applies the default conventional commits configuration
    preCommit {
        // Configuration for pre-commit

    }
    createHooks() // actual hooks creation
}