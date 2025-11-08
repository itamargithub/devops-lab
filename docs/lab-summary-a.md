# 🧭 DevOps Lab: Jenkins + Kubernetes + Helm CI/CD Environment
**Author:** Itamar Galili  
**Purpose:** Practice and demonstrate real-world DevOps automation for CI/CD pipelines using Jenkins, Kubernetes, and Helm — mirroring real production workflows and interview scenarios (H2O.ai focus).

---

## 🧩 Overview

This lab implements a complete local CI/CD system using **Jenkins**, **Kubernetes**, and **Helm**, deployed on **macOS** with **Minikube** and **Docker Desktop**.  
It demonstrates dynamic Jenkins agents, multi-stage Groovy pipelines, Helm-based deployments, and automated rollback/cleanup.

---

## 🧱 Environment Setup Summary

| Component | Purpose | Tool/Version | Notes |
|------------|----------|--------------|-------|
| **macOS** | Local workstation | — | Base OS for lab |
| **Homebrew 4.6.20** | Package manager | — | Installed Python, Terraform, Minikube, etc. |
| **Minikube v1.37.0** | Local Kubernetes cluster | Docker driver | Provides local K8s environment |
| **Docker Desktop 28.4.0** | Container runtime | — | Required for Minikube driver |
| **Jenkins** | CI/CD automation server | Helm chart | Persistent volume keeps config |
| **Helm 3.14.0** | Kubernetes package manager | — | Used to deploy test app |
| **dtzar/helm-kubectl:3.14.0** | Helm + kubectl image | — | Combined image for Helm agent |
| **oh-my-zsh + Powerlevel10k** | Terminal enhancements | zsh | Improved shell experience with autocompletion |

---

## ⚙️ Challenges & Solutions

| # | Challenge | Root Cause | Solution |
|---|------------|-------------|-----------|
| **1** | `brew command not found` | Homebrew missing | Installed via official curl script |
| **2** | `minikube` driver error | Docker daemon not running | Started Docker Desktop service |
| **3** | `No such saved stash 'sysinfo'` | Race condition in parallel Jenkins stages | Split pipeline into sequential stash + parallel analysis |
| **4** | `controller.adminUser` deprecated | Jenkins chart parameter changed | Replaced with `controller.admin.username` |
| **5** | “Cloud” config missing in Jenkins UI | Kubernetes plugin not active | Installed plugin and added Kubernetes cloud |
| **6** | `secrets is forbidden` RBAC error | Jenkins agent lacked permissions | Created `jenkins-admin` ServiceAccount + ClusterRoleBinding |
| **7** | `no template "hello-app.fullname"` | Missing `_helpers.tpl` | Added `_helpers.tpl` with name, fullname, and label templates |
| **8** | `YAML parse error on service.yaml` | Invalid indentation | Rewrote YAML with proper spacing and `nindent 4` |
| **9** | `kubectl: not found` | Helm image lacked kubectl | Switched to `dtzar/helm-kubectl` |
| **10** | Jenkins state lost on restart | No persistent volume | Used default PVC in Helm chart |

---

## 🧩 Pipeline Structure

| Stage | Description | Key Tools / Concepts |
|--------|--------------|----------------------|
| **1. Build & Stash System Info** | Generates metadata on Alpine agent | Groovy, Kubernetes agent, `stash` |
| **2. Parallel Analysis** | Python agent analyzes results, validation runs concurrently | Parallel stages, dynamic agents |
| **3. Deploy with Helm** | Deploys NGINX app to Minikube | Helm, `helm upgrade --install`, service account |
| **4. Rollback & Cleanup** | Rollback on failure or cleanup on success | `helm rollback`, `helm uninstall` |

---

## 🧠 Key Learnings

1. **Dynamic Jenkins Agents**  
   - Created ephemeral agents via inline pod templates (Alpine, Python, Helm).  
   - Learned to manage multiple containers and separate stage environments.

2. **Artifact Sharing Between Pods**  
   - Used `stash` / `unstash` for cross-agent file transfer.  
   - Fixed concurrency issues with proper stage ordering.

3. **RBAC & Permissions Management**  
   - Solved cluster access issues via custom service account `jenkins-admin`.  
   - Practiced secure Kubernetes integrations.

4. **Helm Template Debugging**  
   - Added `_helpers.tpl` and corrected YAML indentation using `nindent`.  
   - Demonstrated debugging real-world Helm rendering errors.

5. **End-to-End CI/CD Flow**  
   - Built, tested, deployed, rolled back, and cleaned up automatically.  
   - Verified results with `kubectl get pods` and Helm release status.

---

## 🧾 Final Validation Commands

After deployment:
```bash
kubectl get pods,svc -n default

