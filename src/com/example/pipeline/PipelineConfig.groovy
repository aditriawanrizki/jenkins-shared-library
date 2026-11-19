
package com.example.pipeline

class PipelineConfig implements Serializable {
    String appName
    String registry
    String registryCredentialsId
    String deployBranch

    PipelineConfig(Map config) {
        this.appName = config.appName ?: 'springboot-devsecops-app'
        this.registry = config.registry ?: 'localhost:8083'
        this.registryCredentialsId =
            config.registryCredentialsId ?: 'nexus-docker-credentials'
        this.deployBranch = config.deployBranch ?: 'main'

        if (!appName?.trim()) {
            throw new IllegalArgumentException('appName is required')
        }

        if (!registry?.trim()) {
            throw new IllegalArgumentException('registry is required')
        }

        if (!registryCredentialsId?.trim()) {
            throw new IllegalArgumentException(
                'registryCredentialsId is required'
            )
        }

        if (!deployBranch?.trim()) {
            throw new IllegalArgumentException('deployBranch is required')
        }
    }
}
