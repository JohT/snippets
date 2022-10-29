#!/usr/bin/env bash

CONTAINER=axoniq/axonserver-quicktest:4.5-SNAPSHOT
AXONSERVER=$(cat ssl/fqdn.txt)
TOKEN=$(cat axonserver.token)
KUBERNETES_NAMESPACE=running-se

Usage () {
    echo "Usage: $0 [<options>]"
    echo ""
    echo "Options:"
    echo "  -a <name>  Axon Server Hostname, default '${AXONSERVER}'."
    echo "  -n <name>  Kubernetes Namespace to deploy to, default '${KUBERNETES_NAMESPACE}'."
    exit 1
}

# Read command line options
options=$(getopt 'a:n:' "$@")
[ $? -eq 0 ] || {
    Usage
}
eval set -- "$options"
while true; do
    case $1 in
    -a)    AXONSERVER=$2            ; shift ;;
    -n)    KUBERNETES_NAMESPACE=$2  ; shift ;;
    --)
        shift
        break
        ;;
    esac
    shift
done
if [[ $# != 0 ]] ; then
    Usage
fi

echo "Running Quicktest using Axon Server hostname ${AXONSERVER}"
echo "on Kubernetes namespace ${KUBERNETES_NAMESPACE}"

kubectl run axonserver-quicktest --image=${CONTAINER} \
--overrides='
{
  "apiVersion": "v1",
  "spec": {
    "containers": [
        {
        "name": "axonserver-quicktest",
        "image": "'${CONTAINER}'",
        "env": [
            {"name": "AXON_AXONSERVER_SERVERS", "value": "'${AXONSERVER}'"},
            {"name": "MS_DELAY", "value": "1000"},
            {"name": "SPRING_PROFILES_ACTIVE", "value": "axonserver"},
            {"name": "AXON_AXONSERVER_SSL-ENABLED", "value": "true"},
            {"name": "AXON_AXONSERVER_CERT-FILE", "value": "/ssl/tls.crt"},
            {"name": "AXON_AXONSERVER_TOKEN", "value": "'${TOKEN}'"}
        ],
        "volumeMounts": [{
            "mountPath": "/ssl/tls.crt",
            "subPath": "tls.crt",
            "readOnly": true,
            "name": "axonserver-cert"
        }]
        }
    ],
    "volumes": [{
        "name":"axonserver-cert",
        "secret": {
        "secretName": "axonserver-tls",
        "items": [{
            "key": "tls.crt",
            "path": "tls.crt"
        }]
        }
    }]
  }
}'    -n ${KUBERNETES_NAMESPACE} --attach stdout --rm