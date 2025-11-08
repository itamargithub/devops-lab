/*
 * groovy-helm-cicd.groovy
 * CI/CD pipeline using dynamic Kubernetes agents and Helm deployment.
 * Author: Itamar Galili
 */

pipeline {
  agent none

  stages {

    stage('Build & Test') {
      parallel {

        stage('Alpine - System Info') {
          agent {
            kubernetes {
              label 'alpine-agent'
              yaml """
apiVersion: v1
kind: Pod
spec:
  containers:
  - name: alpine
    image: alpine
    command:
    - cat
    tty: true
"""
            }
          }
          steps {
            container('alpine') {
              sh '''
                echo "Collecting system info from Alpine agent"
                echo "Host: $(hostname)" > sysinfo.txt
                date >> sysinfo.txt
              '''
            }
            stash includes: 'sysinfo.txt', name: 'sysinfo'
          }
        }

        stage('Python - Analyze Info') {
          agent {
            kubernetes {
              label 'python-agent'
              yaml """
apiVersion: v1
kind: Pod
spec:
  containers:
  - name: python
    image: python:3.10-slim
    command:
    - cat
    tty: true
"""
            }
          }
          steps {
            container('python') {
              unstash 'sysinfo'
              sh '''
                echo "Python container analyzing info..."
                cat sysinfo.txt
              '''
            }
          }
        }

      } // parallel
    } // stage

    stage('Deploy with Helm') {
      agent {
        kubernetes {
          label 'helm-agent'
          yaml """
apiVersion: v1
kind: Pod
spec:
  containers:
  - name: helm
    image: alpine/helm:3.14.0
    command:
    - cat
    tty: true
"""
        }
      }
      steps {
        container('helm') {
          sh '''
            echo "🚀 Deploying hello-app using Helm"
            helm upgrade --install hello-app ./helm/hello-app --namespace default --wait
            kubectl get pods -n default
          '''
        }
      }
    }

  } // stages

  post {
    always {
      echo "✅ CI/CD pipeline completed successfully."
    }
  }
}

