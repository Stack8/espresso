pipeline {
    agent {
        label "master"
    }

    stages {
        stage('build and test') {
            steps {
                script {
                    echo "./gradlew clean build test"
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

        stage('publish') {
            steps {
                script {
                    echo "publish"
                }
            }
        }
    }
}