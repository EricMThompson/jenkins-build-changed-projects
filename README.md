# jenkins-build-changed-projects

Example of a multi-module maven project with a Jenkinsfile that builds only changed modules.


## Jenkins Build Parameters

- `BUILD_ALL`
  set to true to build and deploy all project modules

- `PROJECT LIST`
  build specific project modules (comma separated list)


## Jenkins Prerequisites

The following methods must be approved (Manage Jenkins > In-process Script Approval)

- method java.util.regex.Matcher find  
  (used for finding module names in list of changed files)
