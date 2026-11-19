def call() {
    echo 'Running Spring Boot unit tests'

    sh './mvnw test'

    junit 'target/surefire-reports/*.xml'
}