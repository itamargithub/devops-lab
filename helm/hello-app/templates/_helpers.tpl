{{/*
Expand the name of the chart.
*/}}
{{- define "hello-app.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Create a fully qualified app name.
*/}}
{{- define "hello-app.fullname" -}}
{{- printf "%s" (include "hello-app.name" .) -}}
{{- end -}}

{{/*
Chart labels.
*/}}
{{- define "hello-app.labels" -}}
app.kubernetes.io/name: {{ include "hello-app.name" . }}
helm.sh/chart: {{ .Chart.Name }}-{{ .Chart.Version | replace "+" "_" }}
app.kubernetes.io/instance: {{ .Release.Name }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end -}}

