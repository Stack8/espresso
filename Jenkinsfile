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
                    if (branch != 'main') {
                        echo "Skipping tagging and publishing for non-main branch: ${branch}"
                        return
                    }

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
