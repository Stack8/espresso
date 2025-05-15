pipeline {
    agent {
        label 'master'
    }
    tools {
        jdk 'default'
    }


    stages {
        stage('test') {
            steps {
                script {
                    sh "./gradlew clean build"
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
            when {
                branch 'main'
            }
            steps {
                script {
                    def version = sh(script: "cat ./version.txt", returnStdout: true).trim()
                    def tag = "v${version}"

                    withCredentials([gitUsernamePassword(credentialsId: 'github-http', gitToolName: 'Default')]) {
                        // Clear all tags locally and fetch from origin. If pushing a tag fails for some reason, it will
                        // continue to exist in jenkins even though it won't be present in origin
                        sh "git tag | xargs git tag -d"
                        sh "git fetch --tags"
                        sh "git tag -a ${tag} -m \"espresso version ${tag}\""
                        sh "git push origin ${tag}"
                    }
                }
            }
        }

        stage('publish') {
            when {
                branch 'main'
            }
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
