pipeline {
    agent any

    stages {
        stage('Load env') {
            steps {
                script {
                    withEnv(["$(cat .env | xargs)"]) {
                        sh 'echo $DOCKER_PASSWORD'
                        sh 'echo $DOCKER_USERNAME'
                        sh 'echo $CODECOV_TOKEN'
                    }
                }
            }
        }
        
        stage('Checkout repository') {
            steps {
                cleanWs()
                checkout scm
            }
        }

        stage('Login Docker') {
            steps {
                sh 'echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin'
            }
        }

        stage('Pull Java 17 Docker Image') {
            steps {
                sh 'docker pull openjdk:17-ea-17-jdk-slim'
            }
        }

        stage('Resolve') {
            steps {
                sh 'mvn dependency:go-offline'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean org.jacoco:jacoco-maven-plugin:prepare-agent install -DskipTests -DskipCompile'
            }
        }

        stage('Pack') {
            steps {
                sh 'mvn package -DskipTests -DskipCompile'
            }
        }

        stage('Deploy with Docker') {
            steps {
                sh 'mvn deploy jib:build -P deploy-docker'
            }
        }
    }   

    post {
        always {
            cleanWs()
        }
        script {
            sh 'bash <(curl -s https://codecov.io/bash)'
        }
    }
}