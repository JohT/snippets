#!/usr/bin/env bash

#    Copyright 2021 AxonIQ B.V.

#    Licensed under the Apache License, Version 2.0 (the "License");
#    you may not use this file except in compliance with the License.
#    You may obtain a copy of the License at

#        http://www.apache.org/licenses/LICENSE-2.0

#    Unless required by applicable law or agreed to in writing, software
#    distributed under the License is distributed on an "AS IS" BASIS,
#    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#    See the License for the specific language governing permissions and
#    limitations under the License.

NAMESPACE=running-se
STATEFUL_SET_NAME=axonserver

Usage () {
    echo "Usage: $0 [<options>]"
    echo ""
    echo "Options:"
    echo "  -n <name>  Namespace to deploy to, default '${NAMESPACE}'."
    echo "  -s <name>  Name for the StatefullSet, default '${STATEFUL_SET_NAME}'."
    exit 1
}

options=$(getopt 'n:s:' "$@")
[ $? -eq 0 ] || {
    Usage
}
eval set -- "$options"
while true; do
    case $1 in
    -n)    NAMESPACE=$2     ; shift ;;
    -s)    STATEFUL_SET_NAME=$2      ; shift ;;
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

mkdir -p ssl/${STATEFUL_SET_NAME}

FQDN=${STATEFUL_SET_NAME}.${NAMESPACE}.svc.cluster.local
if [ ! -s ssl/fqdn.txt ] ; then
    echo ""
    echo "Axon Server will be available as ${FQDN}"
    echo ${FQDN} > ssl/fqdn.txt
fi

if [ ! -s ssl/${STATEFUL_SET_NAME}/tls.crt ] ; then
    echo ""
    echo "Generating certificate for '${FQDN}'"
    ./generate-certificate.sh -o ssl/${STATEFUL_SET_NAME}/tls -a ssl/internal-ca ${FQDN}
fi

echo ""
echo "Creating/updating certificate secret"
echo ""
secret=${STATEFUL_SET_NAME}-tls
descriptor=${secret}.yml
kubectl create secret generic ${secret} --from-file=ssl/${STATEFUL_SET_NAME}/tls.key --from-file=ssl/${STATEFUL_SET_NAME}/tls.crt --from-file=ssl/${STATEFUL_SET_NAME}/tls.p12 --dry-run=client -o yaml > ${descriptor}
kubectl apply -f ${descriptor} -n ${NAMESPACE}

echo "Generating files"
echo ""
TEMPLATE=axonserver-sts.yml.tmpl
DESCRIPTOR=${STATEFUL_SET_NAME}.yml
echo "Generating ${DESCRIPTOR}"
sed -e s/__STATEFUL_SET_NAME__/${STATEFUL_SET_NAME}/g \
    -e s/__NAMESPACE__/${NAMESPACE}/g < ${TEMPLATE} > ${DESCRIPTOR}

echo ""
echo "Deploying/updating StatefulSet"
echo ""
kubectl apply -f ${DESCRIPTOR} -n ${NAMESPACE}
