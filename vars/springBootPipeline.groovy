import com.example.pipeline.PipelineConfig

def call(Map config = [:]) {
    def pipelineConfig = new PipelineConfig(config)

    pipeline {
        agent {
            label 'docker-agent'
        }

        options {
            timeout(time: 30, unit: 'MINUTES')
            buildDiscarder(logRotator(
                numToKeepStr: '10',
                artifactNumToKeepStr: '5'
            ))
        }

        stages {
            stage('Test') {
                steps {
                    script {
			def appInfo = libraryResource('app-info.txt')
			echo appInfo
			mavenTest()
                    }
                }
            }

            stage('Build') {
                steps {
                    script {
                        mavenBuild()
                    }
                }
            }

            stage('Deploy') {
                when {
                    branch "${pipelineConfig.deployBranch}"
                }
                steps {
                    script {
                        deployApp([
                            appName: pipelineConfig.appName,
                            registry: pipelineConfig.registry,
                            registryCredentialsId:
                                pipelineConfig.registryCredentialsId
                        ])
                    }
                }
            }
        }

        post {
            always {
                cleanWs()
            }
            success {
                echo 'Pipeline completed successfully.'
            }
            failure {
                echo 'Pipeline failed. Check the stage logs.'
            }
        }
    }
}
