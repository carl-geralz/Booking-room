pipeline {
    agent any
    
    environment {
        POSTGRES_USER = 'postgres'
        POSTGRES_PASSWORD = 'postgres'
        POSTGRES_DB = 'challengebookingroom_db'
    }
    
    stages {
        stage('Checkout repository') {
            steps {
                checkout scm
            }
        }
        
        stage('Set up JDK 21') {
            steps {
                script {
                    def jdkHome = tool name: 'JDK 21', type: 'jdk'
                    env.JAVA_HOME = jdkHome
                    sh "${jdkHome}/bin/java -version"
                }
            }
        }
        
        stage('Cache Maven dependencies') {
            steps {
                cache(path: '.m2/repository', key: 'maven-deps') {
                    sh 'mvn dependency:go-offline'
                }
            }
        }
        
        stage('Build the project') {
            steps {
                sh 'mvn clean package -DskipTests'
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
                    
                    // Wait for PostgreSQL to be ready
                    sh 'docker exec postgres-container pg_isready -U ${POSTGRES_USER} -d ${POSTGRES_DB} -t 30'
                }
            }
        }
        
        stage('Build and Push Docker image') {
            steps {
                script {
                    def dockerImage = docker.build("carlgeralz/challenge-booking-room:latest")
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
            sh 'docker stop postgres-container || true'
            sh 'docker rm postgres-container || true'
        }
    }
}