#!/bin/bash
set -e
brew install minikube kubectl helm
minikube start --driver=docker
kubectl get nodes
