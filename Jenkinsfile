node {
    try {
       stage('Clone') { // for display purposes
          // Get some code from a GitHub repository
          git credentialsId: 'nakza', url: 'https://github.com/naksri379/template.git'
       }
       stage('Build Project') {
                 sh '''
                 chmod +x gradlew
                 ./gradlew clean build
                '''
       }
       stage('Build IntegrationTest') {
                sh '''
                chmod +x gradlew
                ./gradlew -Dspring.profiles.active=test integrationTest
               '''
       }
       stage('Build Docker Image') {
                sh '''
                chmod +x gradlew
                ./gradlew buildArtifact
               '''
       }
       stage('Upload Artifact') {
             withAWS(region:'ap-southeast-1') {
                 s3Upload(bucket:"tescolotus-service-template", includePathPattern:'*.zip', workingDir:'build/libs/')
             }
        }
       stage('Deploy Artifact') {
            def files = findFiles(glob: '**/build/libs/*.zip')
            def ARTIFACT_NAME = files[0].name
            withAWS(region:'ap-southeast-1') {
                sh """\
                    aws elasticbeanstalk create-application-version --region ap-southeast-1 --application-name Testtemp2 --version-label $ARTIFACT_NAME --source-bundle S3Bucket=tescolotus-service-template,S3Key=$ARTIFACT_NAME
                    aws elasticbeanstalk update-environment --region ap-southeast-1 --application-name Testtemp2 --environment-name Testtemp2 --version-label $ARTIFACT_NAME
                """
            }
       }
        reportToMattermost("success")
    } catch (Exception e) {
        reportToMattermost("fail")
        throw e
    }
}

def reportToMattermost(status) {
    def channel = "#cicd"
    if (status == "success") {
        mattermostSend color: "#008000", channel: "${channel}", message: createMattermostMessage(":white_check_mark: Success")
    } else {
        mattermostSend color: "#FF0000", channel: "${channel}", message: createMattermostMessage(":beretparrot: Failure")
    }
}

def createMattermostMessage(statusMessage) {
    "${JOB_NAME} - #${BUILD_NUMBER} ${statusMessage} after ${currentBuild.durationString} [Open](${JENKINS_URL}/job/${JOB_NAME}/${BUILD_NUMBER}/)" as Object
}
