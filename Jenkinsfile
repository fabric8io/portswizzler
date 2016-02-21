#!/usr/bin/groovy
node{
  checkout scm

  def name = 'portswizzler'
  def mavenCentralArtifact = 'io/fabric8/portswizzler/portswizzler'
  
  def stagedProject = stageProject{
    project = name
  }

  String pullRequestId = release {
    projectStagingDetails = stagedProject
    project = name
    helmPush = false
  }

  if (pullRequestId != null){
    waitUntilPullRequestMerged{
      name = name
      prId = pullRequestId
    }
  }

  waitUntilArtifactSyncedWithCentral {
    artifact = mavenCentralArtifact
    version = stagedProject[1]
  }
}
