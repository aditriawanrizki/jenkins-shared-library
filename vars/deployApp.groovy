def call(Map config) {
    echo 'Building and publishing Docker image'

    def imageTag = "${config.appName}:${env.BUILD_NUMBER}"
    def registryImage = "${config.registry}/${imageTag}"

    withCredentials([
        usernamePassword(
            credentialsId: config.registryCredentialsId,
            usernameVariable: 'NEXUS_USER',
            passwordVariable: 'NEXUS_PASSWORD'
        )
    ]) {
        sh """
            set +x
            printf '%s' "\$NEXUS_PASSWORD" | docker login ${config.registry} \
                --username "\$NEXUS_USER" --password-stdin

            docker build -t ${registryImage} .
            docker push ${registryImage}
            docker logout ${config.registry}
        """
    }
}