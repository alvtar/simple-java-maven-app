def branchApi = new URL("https://api.github.com/repos/alvtar/simple-java-maven-app/branches")
def branches = new groovy.json.JsonSlurper().parse(branchApi.newReader())

job("MNTLAB-atarasau-main-build-job"){
  parameters {
    choiceParam('BRANCH_NAME', branches.collect { it.name } , 'branch')
    booleanParam('JOB1', false, 'MNTLAB-atarasau-child1-build-job')
    booleanParam('JOB2', false, 'MNTLAB-atarasau-child2-build-job')
    booleanParam('JOB3', false, 'MNTLAB-atarasau-child3-build-job')
    booleanParam('JOB4', false, 'MNTLAB-atarasau-child4-build-job')
  }
  if ('$JOB1' == true) { CHILD = "MNTLAB-atarasau-child1-build-job"}
  if ('$JOB2' == true) { CHILD = "MNTLAB-atarasau-child2-build-job"}
  if ('$JOB3' == true) { CHILD = "MNTLAB-atarasau-child3-build-job"}
  if ('$JOB4' == true) { CHILD = "MNTLAB-atarasau-child4-build-job"}
  steps {
    downstreamParameterized { // Triggers new parametrized builds.
      trigger('$CHILD') { // Adds a trigger for parametrized builds.
        block { // Blocks until the triggered projects finish their builds.
          buildStepFailure('FAILURE') // Fails the build step if the triggered build is worse or equal to the threshold.
          failure('FAILURE') // Marks this build as failure if the triggered build is worse or equal to the threshold.
          unstable('UNSTABLE') // Mark this build as unstable if the triggered build is worse or equal to the threshold.
        }
        parameters { // Adds parameter values for the projects to trigger.
          currentBuild() // Copies parameters from the current build, except for file parameters.
        }
      }
    }
  }
}

for (number in 1..4) {
  job("MNTLAB-atarasau-child${number}-build-job"){
    parameters {
      stringParam('BRANCH_NAME', '', 'branch name')
    }
    scm {
      git("${GIT_URL}", '$BRANCH_NAME')
    }
    steps {
      shell('echo ${BRANCH_NAME}')
    }
    steps {
      shell('date >> output.log | tar -czf ${BRANCH_NAME}_dsl_script.tar.gz output.log')
    }
    publishers {
      archiveArtifacts {
        pattern('${BRANCH_NAME}_dsl_script.tar.gz')
        onlyIfSuccessful()
      }
    }
  }
}