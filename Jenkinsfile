#!groovy
pipeline {
    agent none

    options {
        buildDiscarder(logRotator(artifactNumToKeepStr: '5', numToKeepStr: '5'))
    }

    stages {
        stage('Build and upload snapshot') {
            agent {
                label 'master'
            }
            steps {
                sh "sudo chmod +x gradlew"
                withCredentials([
                        usernamePassword(credentialsId: 'Artifactory file', passwordVariable: 'ci_pass', usernameVariable: 'continuous_integration'),
                        usernamePassword(credentialsId: 'artifactory-connectors-ci', passwordVariable: 'ci_pass_releases', usernameVariable: 'ci_user_releases')
                ]) {
//                    // parameters for int tests
//                    // TODO enable int tests when we have long-term working sandbox env
//                    -Dhubspot.refreshToken=${bamboo.hubspot.refreshToken.secret} -Dhubspot.clientId=${bamboo.hubspot.clientId} -Dhubspot.clientSecret=${bamboo.hubspot.clientSecret} -Dhubspot.redirectUri=${bamboo.hubspot.redirectUri} -Dhubspot.basicFormId=${bamboo.hubspot.basicFormId} -Dhubspot.notLivePageCampaignId=${bamboo.hubspot.notLivePageCampaignId} -Dhubspot.basicPageId=${bamboo.hubspot.basicPageId} -Dhubspot.archivedPageId=${bamboo.hubspot.archivedPageId} -Dhubspot.notLivePageId=${bamboo.hubspot.notLivePageId} -Dhttps.protocols=TLSv1
                    sh "./gradlew clean build uploadArchives"
                }
            }
            post {
                always {
                    junit "**/test-results/test/*.xml,**/test-results/integration-test/*.xml"
                }
            }
        }
    }

    post {
        unstable {
            slackSend(
                    channel: "#emergency-connectors",
                    color: 'bad',
                    message: "Tests failed: <${env.RUN_DISPLAY_URL}|${env.JOB_NAME} #${env.BUILD_NUMBER}>"
            )
        }

        failure {
            slackSend(
                    channel: "#emergency-connectors",
                    color: 'bad',
                    message: "Build of <${env.RUN_DISPLAY_URL}|${env.JOB_NAME} #${env.BUILD_NUMBER}> is failed!"
            )
        }
    }
}