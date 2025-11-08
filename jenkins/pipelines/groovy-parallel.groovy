/*
 * groovy-parallel.groovy
 * Demonstrates Jenkins dynamic Kubernetes agents (Alpine + Python)
 * with sequential artifact creation and parallel analysis stage.
 * Author: Itamar Galili
 */
§

def sysinfoAvailable = false

pipeline {
  agent none
  stages {

    stage('Collect System Info') {
      agent {
        kubernetes {
          label 'alpine-agent'
          yaml """
apiVersion: v1
kind: Pod
metadata:
  labels:
    job: alpine-test
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
            echo "Alpine container running on: $(hostname)" > sysinfo.txt
            echo "Date: $(date)" >> sysinfo.txt
            echo "✅ Alpine stage complete"
          '''
        }
        stash includes: 'sysinfo.txt', name: 'sysinfo'
      }
    }

    stage('Parallel Analyze') {
      parallel {
        stage('Python Analysis') {
          agent {
            kubernetes {
              label 'python-agent'
              yaml """
apiVersion: v1
kind: Pod
metadata:
  labels:
    job: python-analyze
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
                echo "🐍 Python container running analysis..."
                echo "Contents of sysinfo.txt:"
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
  }

  post {
    always {
      echo "Pipeline finished successfully."
    }
  }
}

