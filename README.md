\# Spring Boot Jenkins Shared Library



\## Overview



Reusable Jenkins Shared Library for testing, building, publishing, and deploying Spring Boot applications using Maven, Docker, Jenkins, and Nexus Repository.



\## Directory Structure



\- `vars/springBootPipeline.groovy`: Main Declarative Pipeline entry point.

\- `vars/mavenTest.groovy`: Executes Maven unit tests and publishes JUnit results.

\- `vars/mavenBuild.groovy`: Packages the Spring Boot application and archives the JAR.

\- `vars/deployApp.groovy`: Builds and publishes the Docker image, then deploys it through SSH.

\- `src/com/example/pipeline/PipelineConfig.groovy`: Pipeline configuration and validation.

\- `resources/app-info.txt`: Shared pipeline resource loaded using `libraryResource`.



\## Configuration



The library accepts the following parameters:



| Parameter | Default |

|---|---|

| `appName` | `springboot-devsecops-app` |

| `registry` | `localhost:8083` |

| `registryCredentialsId` | `nexus-docker-credentials` |

| `deployBranch` | `main` |

| `sshCredentialsId` | `deployment-ssh-credentials` |

| `remoteRegistry` | `nexus:8083` |



\## Pipeline Stages



1\. \*\*Test:\*\* Executes `./mvnw test` and publishes JUnit test results.

2\. \*\*Build:\*\* Packages the application using Maven and archives the generated JAR.

3\. \*\*Deploy:\*\* Runs only on the configured deployment branch. Builds and pushes the Docker image to Nexus, connects to the deployment target over SSH, pulls the image, replaces the running container, and verifies application health through `/actuator/health`.



Pipeline execution stops when a stage fails.



\## Jenkins Configuration



Configure this repository as a Jenkins Global Pipeline Library:



\- Name: `springboot-shared-library`

\- Default version: `v1.0.0`

\- Retrieval method: Modern SCM

\- SCM: Git



Required Jenkins plugins include Pipeline, Pipeline: Groovy Libraries, Git, JUnit, Credentials Binding, and Workspace Cleanup.



\## Credentials



Configure the following Jenkins credentials:



\- `nexus-docker-credentials`: Username and password for the Nexus Docker hosted repository.

\- `deployment-ssh-credentials`: SSH private key for the deployment target, using the `deploy` account.



\## Deployment Architecture



\- Jenkins agent builds and pushes the application image to Nexus.

\- Deployment target authenticates to Nexus and pulls the published image.

\- SSH is used to execute Docker commands on the deployment target.

\- Application is exposed on port `8085`.

\- Health verification uses `/actuator/health`.



\## Registry and Security Notes



The lab uses an HTTP Docker registry connector on port `8083`, configured as an insecure registry in Docker.



This configuration is intended for a controlled test environment. Production deployments should use HTTPS with a trusted TLS certificate.



The deployment target's Docker socket access provides elevated host-level privileges. This lab configuration should not be reused unchanged in production.

