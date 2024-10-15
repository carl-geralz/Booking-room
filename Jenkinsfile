pipeline {
    agent any

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


        stage('Start PostgreSQL container') {
            steps {
                script {
                    docker.image('postgres:14').withRun(
                        '--health-cmd="pg_isready" --health-interval=10s --health-timeout=5s --health-retries=5 ' +
                        '-e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=challengebookingroom_db -p 5432:5432'
                    ) { c ->
                        echo "PostgreSQL container is running"
                    }
                }
            }
        }

        stage('Cache Maven dependencies') {
            steps {
                cache(path: '.m2/repository', key: 'maven-deps') {
                    sh 'mvn dependency:resolve'
                }
            }
        }

        stage('Build the project') {
            steps {
                sh 'mvn package -DskipTests -DskipCompile'
            }
        }

        stage('Build Docker image') {
            steps {
                sh 'docker build -t challenge-booking-room:latest .'
            }
        }

        stage('Deploy with Docker Compose') {
            when {
                branch 'devops'
            }
            steps {
                sh 'docker compose -f docker-compose.yml up -d --build'
            }
        }

        stage('Analyze for vulnerabilities') {
            when {
                branch 'devops'
            }
            steps {
                script {
                    sh '''
                    docker scout cves challenge-booking-room:latest --output sarif --file sarif.output.json
                    '''
                    archiveArtifacts 'sarif.output.json'
                }
            }
        }
    }
}
