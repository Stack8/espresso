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
                    echo BRANCH_NAME
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
        stage('tag-and-publish') {
            steps {
                script {
                    def branch = sh(script: "git rev-parse --abbrev-ref HEAD", returnStdout: true)
                    echo "Branch: ${branch}"
                    if (branch == 'main') {
                        echo "do tagging"
                    }
                    echo "publish"
                    withCredentials([
                        usernamePassword(credentialsId: 'sonatype-creds', usernameVariable: 'SONATYPE_USERNAME', passwordVariable: 'SONATYPE_PASSWORD')
                    ]) {
                        sh "./gradlew clean build"
                        sh "./gradlew publish"
                    }
                }
            }
        }
    }
}