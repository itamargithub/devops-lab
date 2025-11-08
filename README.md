Perfect, Itamar 🙌 — you’re building a **DevOps lab repository** that reflects exactly what you’ve achieved so far:

* ✅ local Kubernetes cluster (Minikube) on macOS
* ✅ Jenkins running inside the cluster via Helm (with persistent storage)
* ✅ Kubernetes plugin integration (working with service account + dynamic pod agents)
* ✅ Custom terminal setup (Oh My Zsh + Powerlevel10k for AWS/K8s awareness)
* ✅ Base for Groovy pipelines and CI/CD experimentation

Here’s a **complete, clear, professional README.md** you can copy straight into your GitHub repo (`devops-lab/README.md`):

---

````markdown
# 🚀 DevOps Lab – Jenkins on Kubernetes (Minikube)

This repository documents my personal **DevOps lab environment**, built on macOS using **Minikube**, **Helm**, **Jenkins**, and **Kubernetes**.  
It serves as both a reference and a reusable playground for CI/CD, Groovy pipelines, AWS/K8s integration, and DevOps automation.

---

## 🧠 Overview

The goal of this setup is to simulate a realistic DevOps environment — entirely locally — to practice:
- Jenkins pipeline development (Groovy)
- Kubernetes integration with Jenkins agents
- Helm-based application deployment
- Persistent storage configuration
- Cluster administration and CI/CD design

---

## 🧩 Environment Setup

### 🖥️ Host
- **macOS (Apple Silicon / arm64)**
- **Homebrew 4.6.20**
- **Docker Desktop** (as Minikube driver)
- **Minikube v1.37.0**
- **Kubernetes v1.34.0**
- **Helm v3**
- **kubectl**
- **Terraform 1.5.7**
- **Python 3.14**
- **Oh My Zsh + Powerlevel10k (customized prompt)**

---

## ☸️ Kubernetes Cluster Setup

```bash
# Start Minikube with Docker driver
minikube start --driver=docker

# Verify
kubectl get nodes
kubectl config current-context  # should show 'minikube'
````

**Addons enabled:**

* `storage-provisioner`
* `default-storageclass`

---

## 🧱 Jenkins Deployment via Helm

### Create Namespace & PVC

```bash
kubectl create namespace jenkins

cat <<EOF | kubectl apply -f -
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: jenkins-pv-claim
  namespace: jenkins
spec:
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 5Gi
EOF
```

### Install Jenkins Helm Chart

```bash
helm repo add jenkins https://charts.jenkins.io
helm repo update

helm install jenkins jenkins/jenkins \
  --namespace jenkins \
  --set controller.admin.username=admin \
  --set controller.admin.password=admin123 \
  --set controller.serviceType=NodePort \
  --set persistence.existingClaim=jenkins-pv-claim \
  --set persistence.enabled=true \
  --set persistence.storageClass="" \
  --set persistence.size=5Gi
```

**Access Jenkins:**

```bash
minikube service jenkins -n jenkins
```

**Login credentials:**

```
admin / admin123
```

---

## 🔐 Jenkins Configuration

### Install Plugins

* **Kubernetes Plugin**
* **Kubernetes CLI**
* **Workflow Aggregator**

### Add Kubernetes Cloud

**Manage Jenkins → System → Cloud → Kubernetes**

| Field          | Value                                                                                          |
| -------------- | ---------------------------------------------------------------------------------------------- |
| Name           | minikube                                                                                       |
| Kubernetes URL | [https://kubernetes.default.svc](https://kubernetes.default.svc)                               |
| Namespace      | jenkins                                                                                        |
| Credentials    | Kubernetes Service Account (`minikube-sa`)                                                     |
| Jenkins URL    | [http://jenkins.jenkins.svc.cluster.local:8080](http://jenkins.jenkins.svc.cluster.local:8080) |

### Grant Permissions

```bash
kubectl create clusterrolebinding jenkins-admin-binding \
  --clusterrole=cluster-admin \
  --serviceaccount=jenkins:default
```

✅ Click **Test Connection** → should say *Connected to Kubernetes v1.xx*

---

## 🧰 Jenkins Agent (Pod Template Example)

**Pod Template**

* Name: `alpine-agent`
* Label: `alpine`
* Image: `alpine`
* Command: `cat`
* TTY: ✅ enabled

---

## 🧪 Sample Pipeline (Groovy Jenkinsfile)

```groovy
pipeline {
  agent {
    kubernetes {
      label 'alpine'
    }
  }
  stages {
    stage('Test') {
      steps {
        container('alpine') {
          sh '''
            echo "✅ Running on Kubernetes inside Minikube!"
            echo "Node: $(hostname)"
            echo "Date: $(date)"
          '''
        }
      }
    }
  }
  post {
    always {
      echo "Pipeline completed."
    }
  }
}
```

Run this pipeline from Jenkins → New Item → Pipeline → Paste script → Build Now.

---

## 💾 Persistence Test

To confirm Jenkins data survives restarts:

```bash
kubectl delete pod -n jenkins -l app.kubernetes.io/component=jenkins-controller
kubectl get pods -n jenkins
```

All jobs, plugins, and credentials remain intact (PVC verified).

---

## 🧭 Powerlevel10k DevOps Shell

Customized `.zshrc` and Powerlevel10k configuration include:

* AWS profile display (`☁️ dev:eu-central-1`)
* Kubernetes context (`☸️ minikube`)
* Python virtualenv (`🐍 venv`)
* Git branch (` main`)
* Command timing and right-aligned time display

---

## 🗂️ Repository Structure

```
devops-lab/
├── README.md
├── jenkins/
│   ├── Jenkinsfile
│   ├── jenkins-pv.yaml
│   ├── values.yaml
│   └── jenkins-notes.md
├── k8s/
│   ├── deployment.yaml
│   ├── service.yaml
│   └── ingress.yaml
├── scripts/
│   ├── setup_minikube.sh
│   ├── setup_jenkins.sh
│   └── utilities.sh
└── docs/
    ├── 01_installation.md
    ├── 02_jenkins_k8s_integration.md
    ├── 03_groovy_examples.md
    └── 04_ci_cd_helm_pipeline.md
```



