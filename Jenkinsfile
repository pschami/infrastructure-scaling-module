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
                sh "mvn compile"
            }
        }
        stage('Unit Testing') {
            steps {
                sh "mvn test"
            }
        }
        stage('Deploy') {
            when {
                expression {
                    return env.BRANCH_NAME ==~ /master|release\/.*/
                }
            }
            steps {
                sh "mvn clean deploy"
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
                    sh "sh /opt/nexB/scancode --format html ${WORKSPACE} /opt/nexB/nexb-output/api-gateway-parent.html"
		    sh "sh /opt/nexB/scancode --format html-app ${WORKSPACE} /opt/nexB/nexb-output/api-gateway-parent-grap.html"
	       
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
    }
    post {
        always{
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
