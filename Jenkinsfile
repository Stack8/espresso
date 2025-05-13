pipeline {
    agent {
        label "master"
    }

    tools {
        jdk 'default'
    }


    stages {
        stage('temp') {
            steps {
                script {
                    echo "temp"
                    sh "git rev-parse --abbrev-ref HEAD"
                }
            }
        }
        stage('test') {
            steps {
                script {
                    sh "./gradlew clean build test"
                }
            }
            post {
                always {
                   archiveArtifacts allowEmptyArchive: true,
                           artifacts: '**/reports/tests/**'
               }
            }
        }

        // Note: tagging stage MUST come before publishing to prevent a new artifact from overwriting an existing
        // artifact for a specific version. Tagging will fail if you attempt to duplicate a tag, and prevents a duplicated
        // artifact from being published
        stage('tag') {
            steps {
                script {
                    echo "tag"
                }
            }
        }

        stage('build') {
            steps {
                script {
                    echo "build"
                }
            }
        }

        stage('publish') {
            steps {
                script {
                    withCredentials([
                        usernamePassword(credentialsId: 'sonatype-creds', usernameVariable: 'SONATYPE_USERNAME', passwordVariable: 'SONATYPE_PASSWORD')
                    ]) {
                        sh "./gradlew publish"
                    }
                }
            }
        }
    }
}