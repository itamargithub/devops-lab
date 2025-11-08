/*
 * groovy-helm-cicd.groovy
 * CI/CD pipeline using dynamic Kubernetes agents and Helm deployment.
 * Author: Itamar Galili
 */

pipeline {
  agent none

  stages {

    // ------------------- STAGE 1 -------------------
    stage('Build & Stash System Info') {
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
            echo "Collecting system info from Alpine agent..."
            echo "Host: $(hostname)" > sysinfo.txt
            date >> sysinfo.txt
          '''
        }
        stash includes: 'sysinfo.txt', name: 'sysinfo'
      }
    }

    // ------------------- STAGE 2 -------------------
    stage('Parallel Analysis') {
      parallel {

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
                echo "🐍 Python container analyzing sysinfo..."
                cat sysinfo.txt
              '''
            }
          }
        }

        stage('Validation') {
          agent any
          steps {
            echo "🧩 Validation or parallel check running..."
          }
        }
      }
    }

    // ------------------- STAGE 3 -------------------
    stage('Deploy with Helm') {
      agent {
        kubernetes {
          label 'helm-agent'
          yaml """
apiVersion: v1
kind: Pod
spec:
  #serviceAccountName: default
  serviceAccountName: jenkins-admin
  containers:
  - name: helm
    #image: alpine/helm:3.14.0
    image: dtzar/helm-kubectl:3.14.0
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
            echo "✅ Deployment complete — current pods:"
            kubectl get pods -n default
          '''
        }
      }
    }
  }

  post {
    always {
      echo "✅ CI/CD pipeline completed successfully."
    }
  }
}

