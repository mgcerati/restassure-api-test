pipeline{
    agent any
    stages
            {
                stage('Download code')
                        {
                            steps{
                                git 'https://github.com/mgcerati/restassure-api-test.git'
                            }

                        }

                stage('Run Tests')
                        {
                            steps{
                                withGradle{
                                    sh '/var/jenkins_home/tools/hudson.plugins.gradle.GradleInstallation/gradle6.8/bin/gradle clean test'
                                }
                            }
                        }
            }

    post{
        always{
            script{
                allure([
                        includeProperties: false,
                        jdk:'',
                        properties:[],
                        reportBuildPolicy:'ALWAYS',
                        results:[[path:'build/allure-results']]
                ])
            }
        }
    }
}