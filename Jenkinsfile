pipeline {
    agent {
        label "master"
    }

    stages {
        stage('build') {
            script {
                echo "build"
            }
        }

        stage('test') {
            script {
                echo "test"
            }
        }

        // Note: tagging stage MUST come before publishing to prevent a new artifact from overwriting an existing
        // artifact for a specific version. Tagging will fail if you attempt to duplicate a tag, and prevents a duplicated
        // artifact from being published
        stage('tag') {
            script {
                echo "tag"
            }
        }

        stage('publish') {
            script {
                echo "publish"
            }
        }
    }
}