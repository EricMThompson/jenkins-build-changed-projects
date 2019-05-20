/**
 * Example Jenkinsfile for building and deploying only changed project modules.
 * The dependencies and dependents of each changed module will also be built.
 *
 * Parameters:
 * BUILD_ALL
 *   set to true to build and deploy all project modules
 * PROJECT LIST
 *   build specific project modules (comma separated list)
 *
 * See also:
 * deployable-modules.txt
 *   List of modules that can be deployed. Some modules are libraries that should not be deployed.
 *
 * Prerequisites:
 * The following methods must be approved (Manage Jenkins > In-process Script Approval)
 *   - method java.util.regex.Matcher find
 *
 */

import java.util.regex.Matcher
import java.util.regex.Pattern

/** Prefix that identifies a module directory */
String modulePrefix = "module-"

/** Modules that will be built */
ArrayList<String> builtModules = []

/** Maven project list arguments for specifying which modules to build */
String projectListArgs = ""

pipeline {

    agent any

    parameters {
        booleanParam(
                name: "BUILD_ALL",
                defaultValue: false,
                description: 'Build all modules (Overrides PROJECT_LIST)'
        )
        string(
                name: "PROJECT_LIST",
                defaultValue: "",
                description: 'Maven project list (Comma separated list of modules to build)'
        )
    }

    stages {

        stage('build') {
            steps {
                script {
                    boolean nothingToBuild = false
                    if (params.BUILD_ALL) {
                        // Build every module
                        echo "Building all modules"
                        projectListArgs = ""
                    } else if (params.PROJECT_LIST != "") {
                        // Build specific modules
                        echo "Building specified modules: ${params.PROJECT_LIST}"
                        projectListArgs = "-am -amd -pl " + params.PROJECT_LIST
                    } else {
                        // Build only changed modules
                        echo "Building changed modules"
                        def changedModules = getChangedModules(modulePrefix)
                        projectListArgs = "-am -amd -pl " + changedModules.join(",")
                        nothingToBuild = changedModules.size() == 0
                    }

                    if (nothingToBuild) {
                        echo "Nothing to build"
                    } else {
                        // Determine which modules will be built using `mvn validate`
                        builtModules = sh(
                                returnStdout: true,
                                script: "mvn -B validate ${projectListArgs} | " +
                                        "grep -Po \"${modulePrefix}.*?(?= .*SUCCESS)\""
                        ).split("\n")

                        sh "mvn -B clean package ${projectListArgs}"
                    }
                }
            }
        }

        stage('list changed modules') {
            steps {
                script {
                    ArrayList<String> deployableModules = readFile(file: "deployable-modules.txt").split("\n")
                    for (String module in builtModules) {
                        if (deployableModules.contains(module)) {

                            // TODO deployment code here
                            echo "DEPLOY " + module

                        }
                    }
                }
            }
        }

    }

}

/** Get a list of modules that have changed since the last successful build */
def getChangedModules(String modulePrefix) {
    // Get changed files since last successful build
    changedFiles = []
    build = currentBuild
    while (build != null && build.result != 'SUCCESS') { // for all builds since last success
        for (changeLogSet in build.changeSets) {
            for (entry in changeLogSet.getItems()) { // for each commit in the detected changes
                for (file in entry.getAffectedFiles()) {
                    changedFiles.add(file.getPath()) // add changed file to list
                }
            }
        }
        build = build.previousBuild
    }
    // Find distinct modules from changed files
    HashSet<String> modules = new HashSet<>()
    Pattern regex = ~/(${modulePrefix}.*?)\//
    for (String path in changedFiles) {
        Matcher matcher = regex.matcher(path)
        while (matcher.find()) {
            modules.add(matcher.group(1))
        }
    }
    return new ArrayList<>(modules)
}
