\# Spring Boot Jenkins Shared Library



\## Overview



Reusable Jenkins Shared Library for building and deploying

Spring Boot applications using Maven and Docker.



\## Directory Structure



\- `vars/springBootPipeline.groovy`: Main Declarative Pipeline entry point.

\- `vars/mavenTest.groovy`: Executes Maven unit tests.

\- `vars/mavenBuild.groovy`: Packages the Spring Boot application.

\- `vars/deployApp.groovy`: Builds and publishes the Docker image.

\- `src/com/example/pipeline/PipelineConfig.groovy`: Configuration and validation.

\- `resources/app-info.txt`: Shared pipeline resource.



\## Configuration



The library accepts the following parameters:



| Parameter | Default |

|---|---|

| appName | springboot-devsecops-app |

| registry | localhost:8083 |

| registryCredentialsId | nexus-docker-credentials |

| deployBranch | main |



\## Pipeline Stages



1\. Test: Executes `./mvnw test`.

2\. Build: Packages the application and archives the JAR.

3\. Deploy: Builds and pushes the Docker image when running on the configured deployment branch.



\## Jenkins Configuration



Configure this repository as a Jenkins Global Pipeline Library:



\- Name: `springboot-shared-library`

\- Default version: `v1.0.0`

\- Retrieval method: Modern SCM

\- SCM: Git

