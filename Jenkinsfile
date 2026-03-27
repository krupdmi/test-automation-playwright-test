pipeline {
    agent any

    parameters {
        choice(
            name: 'ENVIRONMENT',
            choices: ['dev', 'int', 'uat'],
            description: 'Target environment to run tests against'
        )
        choice(
            name: 'MODULE',
            choices: ['all', 'api', 'ui'],
            description: 'Which test module to execute'
        )
        string(
            name: 'TAGS',
            defaultValue: '@smoke',
            description: 'Cucumber tag expression — e.g. @smoke, @regression, @smoke and @api'
        )
        string(
            name: 'THREAD_COUNT',
            defaultValue: '4',
            description: 'Number of parallel threads'
        )
        booleanParam(
            name: 'HEADLESS',
            defaultValue: true,
            description: 'Run browser in headless mode (UI tests only)'
        )
    }

    environment {
        JAVA_HOME   = tool 'JDK-21'
        MAVEN_HOME  = tool 'Maven-3'
        PATH        = "${JAVA_HOME}/bin:${MAVEN_HOME}/bin:${PATH}"
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '20'))
        timeout(time: 2, unit: 'HOURS')
        timestamps()
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build commons') {
            steps {
                sh 'mvn clean install -pl test-automation-commons -am -DskipTests -q'
            }
        }

        stage('Run tests') {
            steps {
                script {
                    def mavenModule = params.MODULE == 'all'
                        ? 'test-automation-e2e-api,test-automation-e2e-ui'
                        : "test-automation-e2e-${params.MODULE}"

                    sh """
                        mvn test \
                            -pl ${mavenModule} \
                            -am \
                            -Dspring.profiles.active=${params.ENVIRONMENT},endpoints \
                            -Dcucumber.filter.tags="${params.TAGS}" \
                            -Dparallel.threads=${params.THREAD_COUNT} \
                            -Dbrowser.headless=${params.HEADLESS} \
                            -Djenkins.agent.name=${env.NODE_NAME ?: 'jenkins'}
                    """
                }
            }
        }

        stage('Aggregate Allure results') {
            steps {
                sh 'mvn antrun:run@aggregate-allure-results -q || true'
            }
        }
    }

    post {
        always {
            allure([
                includeProperties: false,
                jdk: '',
                properties: [],
                reportBuildPolicy: 'ALWAYS',
                results: [[path: 'allure-results']]
            ])
            junit(
                testResults: '**/target/cucumber-reports/*.xml',
                allowEmptyResults: true
            )
            archiveArtifacts(
                artifacts: '**/target/cucumber-reports/**',
                allowEmptyArchive: true
            )
        }
        failure {
            echo "Pipeline failed — check Allure report for details."
        }
        cleanup {
            cleanWs()
        }
    }
}
