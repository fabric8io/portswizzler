#!/usr/bin/groovy
node{
  checkout scm

  def name = 'portswizzler'
  def mavenCentralArtifact = 'io/fabric8/portswizzler/portswizzler'

  kubernetes.pod('buildpod').withImage('maven:3.3.3').inside {

    def stagedProject = stageProject{
      project = name
    }

    String pullRequestId = release {
      projectStagingDetails = stagedProject
      project = name
      helmPush = false
    }

    waitUntilPullRequestMerged{
      name = name
      prId = pullRequestId
    }

    waitUntilArtifactSyncedWithCentral {
      artifact = mavenCentralArtifact
      version = stagedProject[1]
    }
  }
}
