pipeline {
    agent any

    environment {
        POSTGRES_USER = 'postgres'
        POSTGRES_PASSWORD = 'postgres'
        POSTGRES_DB = 'challengebookingroom_db'
    }

    tools {
        jdk 'JDK 17'
        maven 'maven'
        dockerTool 'docker'
    }

    stages {
        stage('Checkout repository') {
            steps {
                cleanWs()
                checkout scm
            }
        }

        stage('Build the project') {
            steps {
                sh 'mvn dependency:go-offline && mvn package -DskipTests -DskipCompile'
            }
        }

        stage('Start PostgreSQL container') {
            steps {
                script {
                    sh """
                    docker run -d --name postgres-container \
                        -e POSTGRES_USER=${POSTGRES_USER} \
                        -e POSTGRES_PASSWORD=${POSTGRES_PASSWORD} \
                        -e POSTGRES_DB=${POSTGRES_DB} \
                        -p 5432:5432 \
                        postgres:14
                    """
                    
                    sh 'docker exec postgres-container pg_isready -U ${POSTGRES_USER} -d ${POSTGRES_DB} -t 30'
                }
            }
        }
        

        stage('Build Docker image using DinD') {
            agent {
                docker {
                    image 'docker:27.3.1-dind' // This pulls the Docker-in-Docker image
                    args '--privileged' // This allows Docker to run within Docker
                }
            }
            steps {
                script {
                    def dockerImage = docker.build("carlgeralz/challenge-booking-room:${env.BUILD_NUMBER}")
                    docker.withRegistry('https://registry.hub.docker.com', 'docker-hub-credentials') {
                        dockerImage.push()
                        dockerImage.push('latest')
                    }
                }
            }
        }
        
        stage('Deploy with Docker Compose') {
            when {
                branch 'devops'
            }
            steps {
                sh 'docker-compose -f docker-compose.yml up -d --build'
            }
        }
    }

    post {
        always {
            cleanWs()
            sh 'docker stop postgres-container || true'
            sh 'docker rm postgres-container || true'
        }
    }
}
