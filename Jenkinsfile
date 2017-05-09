properties([
    pipelineTriggers([
      upstream(threshold: hudson.model.Result.SUCCESS, upstreamProjects: 'common-client-parent-master'),
      upstream(threshold: hudson.model.Result.SUCCESS, upstreamProjects: 'common-messaging-parent-master'),
      upstream(threshold: hudson.model.Result.SUCCESS, upstreamProjects: 'hdp-capability-registry-client-master'),
      upstream(threshold: hudson.model.Result.SUCCESS, upstreamProjects: 'symphony-root-parent-master'),
      upstream(threshold: hudson.model.Result.SUCCESS, upstreamProjects: 'virtualization-capabilities-api-master'),
      upstream(threshold: hudson.model.Result.SUCCESS, upstreamProjects: 'coprhd-adapter-parent-master'),
      upstream(threshold: hudson.model.Result.SUCCESS, upstreamProjects: 'rackhd-adapter-parent-master')
  ])
])
pipeline {    
    agent {
        node{
            label 'maven-builder'
            customWorkspace "workspace/${env.JOB_NAME}"
            }
    }
    environment {
        GITHUB_TOKEN = credentials('github-02')
    }
    options { 
        buildDiscarder(logRotator(artifactDaysToKeepStr: '30', artifactNumToKeepStr: '5', daysToKeepStr: '30', numToKeepStr: '5'))
        timestamps()
    }
    tools {
        maven 'linux-maven-3.3.9'
        jdk 'linux-jdk1.8.0_102'
    }
    stages {
        stage('Compile') {
            steps {
                sh "mvn -U clean compile -DskipTests=true -DskipITs"
            }
        }
        stage('Prepare test services') {
            steps {
                sh "docker-compose -f ${WORKSPACE}/ci/docker/docker-compose.yml pull"
                sh "docker-compose -f ${WORKSPACE}/ci/docker/docker-compose.yml up --force-recreate -d"
            }
        }
        stage('Integration Test') {
            steps {
                sh "docker exec scaling-module-test-${BUILD_NUMBER} mvn clean verify -DskipDocker=true"
            }
        }
	stage('Package') {
		steps {
                sh "mvn package -DskipTests -DskipITs"
            }
	}
        stage('Deploy') {
	    when {
                expression {
                    return env.BRANCH_NAME ==~ /master|release\/.*/
                }
            }
            steps {
                sh "mvn help:active-profiles deploy -DskipTests -DskipITs -P buildDockerImageOnJenkins -Ddocker.registry=docker-dev-local.art.local"
            }
        }
        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') { 
                    sh '''
                            mvn sonar:sonar \
                                -Dsonar.host.url=$SONAR_HOST_URL \
                                -Dsonar.login=$SONAR_AUTH_TOKEN \
                                -Dsonar.java.coveragePlugin=jacoco \
                                -Dsonar.junit.reportsPath=target/surefire-reports \
                                -Dsonar.jacoco.reportPath=target/coverage-reports/jacoco-ut.exec \
                                -Dsonar.jacoco.itReportPath=target/coverage-reports/jacoco-it.exec \
                                -Dsonar.dependencyCheck.reportPath=${WORKSPACE}/report/dependency-check-report.xml
                        '''    
                }
            }
        }
        stage('NexB Scan') {
	    when {
                expression {
                    return env.BRANCH_NAME ==~ /master|release\/.*/
                }
            }
            steps {
                dir('/opt') {
                    checkout([$class: 'GitSCM', 
                              branches: [[name: '*/master']], 
                              doGenerateSubmoduleConfigurations: false, 
                              extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'nexB']], 
                              submoduleCfg: [], 
                              userRemoteConfigs: [[url: 'https://github.com/nexB/scancode-toolkit.git']]])
                }				
            	sh "mkdir -p /opt/nexB/nexb-output/"
                sh "sh /opt/nexB/scancode --help"
                sh "sh /opt/nexB/scancode --format html ${WORKSPACE} /opt/nexB/nexb-output/scaling-module-parent.html"
                sh "sh /opt/nexB/scancode --format html-app ${WORKSPACE} /opt/nexB/nexb-output/scaling-module-parent-grap.html"	       
	        sh "mv /opt/nexB/nexb-output/ ${WORKSPACE}/"
	       	archiveArtifacts '**/nexb-output/**'
            }
        }
        stage('Third Party Audit') {
            steps {
                sh '''
                    mvn org.apache.maven.plugins:maven-dependency-plugin:2.10:analyze-report license:add-third-party org.apache.maven.plugins:maven-dependency-plugin:2.10:tree \
                    -DoutputType=dot \
                    -DoutputFile=${WORKSPACE}/report/dependency-tree.dot
                    '''   
                archiveArtifacts '**/dependency-analysis.html, **/THIRD-PARTY.txt, **/dependency-check-report.html, **/dependency-tree.dot'
            }
        }
        stage('Github Release') {
            when {
                expression {
                    return env.BRANCH_NAME ==~ /release\/.*/
                }
            }
            steps {
                dir('/opt'){
                    sh "rm -f linux-amd64-github-release.tar.bz2"
                    sh "wget https://github.com/aktau/github-release/releases/download/v0.7.2/linux-amd64-github-release.tar.bz2"
                    sh "tar -xvjf linux-amd64-github-release.tar.bz2"
                    sh "yes | cp bin/linux/amd64/github-release /usr/bin/"
                    sh '''
                        github-release release \
                            --user dellemc-symphony \
                            --repo  scaling-module-parent \
                            --tag v0.0.1-${BRANCH_NAME}-${BUILD_ID} \
                            --name "scaling-module-parent release" \
                            --description "scaling-module-parent release"
                        github-release upload \
                            --user dellemc-symphony \
                            --repo scaling-module-parent \
                            --tag v0.0.1-${BRANCH_NAME}-${BUILD_ID} \
                            --name "scaling-module-parent release" \
                            --file ${WORKSPACE}/target/scaling-module-parent-1.0-SNAPSHOT.jar
                    '''
                }
            }
        }
    }
    post {
        always{
            sh "docker-compose -f ${WORKSPACE}/ci/docker/docker-compose.yml down --rmi 'all' -v --remove-orphans"
            step([$class: 'WsCleanup'])   
        }
        success {
            emailext attachLog: true, 
                body: 'Pipeline job ${JOB_NAME} success. Build URL: ${BUILD_URL}', 
                recipientProviders: [[$class: 'CulpritsRecipientProvider']], 
                subject: 'SUCCESS: Jenkins Job- ${JOB_NAME} Build No- ${BUILD_NUMBER}', 
                to: 'pebuildrelease@vce.com'            
        }
        failure {
            emailext attachLog: true, 
                body: 'Pipeline job ${JOB_NAME} failed. Build URL: ${BUILD_URL}', 
                recipientProviders: [[$class: 'CulpritsRecipientProvider'], [$class: 'DevelopersRecipientProvider'], [$class: 'FailingTestSuspectsRecipientProvider'], [$class: 'UpstreamComitterRecipientProvider']], 
                subject: 'FAILED: Jenkins Job- ${JOB_NAME} Build No- ${BUILD_NUMBER}', 
                to: 'pebuildrelease@vce.com'
        }
    }
}
