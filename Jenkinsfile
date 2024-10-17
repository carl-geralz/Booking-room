pipeline {
    agent any

    environment {
        POSTGRES_USER = 'postgres'
        POSTGRES_PASSWORD = 'postgres'
        POSTGRES_DB = 'challengebookingroom_db'
    }

    tools {

    }

    stages {
        stage('Checkout repository') {
            steps {
                cleanWs()
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'mvn dependency:go-offline && mvn clean install -DskipTests'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Deploy') {
            steps {
                sh 'mvn package -DskipTests -DskipCompile'
            }
        }

    post {
        always {
            cleanWs()
        }
    }
}
}