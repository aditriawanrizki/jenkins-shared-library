def call() {
    echo 'Building Spring Boot application'

    sh './mvnw clean package -DskipTests'

    archiveArtifacts artifacts: 'target/*.jar',
                     fingerprint: true
}