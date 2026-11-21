
def call(Map config) {
    def imageTag = "${config.appName}:${env.BUILD_NUMBER}"
    def registryImage = "${config.registry}/${imageTag}"
    def remoteRegistryImage = "${config.remoteRegistry}/${imageTag}"
    def remoteHost = 'host.docker.internal'
    def remotePort = '2222'
    def containerName = config.appName
    def appPort = '8085'

    echo "Building and publishing ${registryImage}"

    withCredentials([
        usernamePassword(
            credentialsId: config.registryCredentialsId,
            usernameVariable: 'NEXUS_USER',
            passwordVariable: 'NEXUS_PASSWORD'
        ),
        sshUserPrivateKey(
            credentialsId: config.sshCredentialsId,
            keyFileVariable: 'SSH_KEY',
            usernameVariable: 'SSH_USER'
        )
    ]) {
        sh """
            set +x

            # Build and push image from Jenkins agent
            printf '%s' "\$NEXUS_PASSWORD" | docker login ${config.registry} \
                --username "\$NEXUS_USER" --password-stdin

            docker build -t ${registryImage} .
            docker push ${registryImage}
            docker logout ${config.registry}

            # Authenticate remote Docker client to Nexus
            printf '%s' "\$NEXUS_PASSWORD" | \
                ssh -i "\$SSH_KEY" -p ${remotePort} -o BatchMode=yes \
                "\$SSH_USER@${remoteHost}" \
                "docker login ${config.remoteRegistry} --username '\$NEXUS_USER' --password-stdin"

            # Pull the new image on the deployment target
            ssh -i "\$SSH_KEY" -p ${remotePort} \
                -o BatchMode=yes "\$SSH_USER@${remoteHost}" \
                "docker pull ${remoteRegistryImage}"

            # Replace the running application container
            ssh -i "\$SSH_KEY" -p ${remotePort} \
                -o BatchMode=yes "\$SSH_USER@${remoteHost}" \
                "docker rm -f ${containerName} || true"

            ssh -i "\$SSH_KEY" -p ${remotePort} \
                -o BatchMode=yes "\$SSH_USER@${remoteHost}" \
                "docker run -d --name ${containerName} \
                --restart unless-stopped \
                -p ${appPort}:8080 ${remoteRegistryImage}"

            ssh -i "\$SSH_KEY" -p ${remotePort} \
                -o BatchMode=yes "\$SSH_USER@${remoteHost}" \
                "docker logout ${config.remoteRegistry}"

            # Verify application health with retries
            for attempt in \$(seq 1 12); do
                if curl -fsS http://${remoteHost}:${appPort}/actuator/health; then
                    echo "Application health check successful."
                    exit 0
                fi

                echo "Waiting for application startup: attempt \$attempt/12"
                sleep 5
            done

            echo "Application health check failed."
            exit 1
        """
    }
}
