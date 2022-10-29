#!/usr/bin/env bash

#    Copyright 2020,2021 AxonIQ B.V.

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

Usage () {
    echo "Usage: $0 [<options>]"
    echo ""
    echo "Options:"
    echo "  -n <name>  Namespace to deploy to, default '${NAMESPACE}'."
    exit 1
}

options=$(getopt 'n:' "$@")
[ $? -eq 0 ] || {
    Usage
}
eval set -- "$options"
while true; do
    case $1 in
    -n)    NAMESPACE=$2       ; shift ;;
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

if [[ "${PUBLIC_DOMAIN}" == "" ]] ; then
    PUBLIC_DOMAIN=${NAMESPACE}.svc.cluster.local
fi

APP_TOKEN_FILE=./axonserver.token
ADMIN_TOKEN_FILE=./axonserver.admin-token
echo "Generating tokens"
echo ""
./generate-token.sh ${APP_TOKEN_FILE}
./generate-token.sh ${ADMIN_TOKEN_FILE}

mkdir -p ssl

APP_TOKEN=$(cat ${APP_TOKEN_FILE})
ADMIN_TOKEN=$(cat ${ADMIN_TOKEN_FILE})
echo ""
echo "Generating files"
echo ""
for src in axonserver.properties.tmpl ; do
    dst=$(basename ${src} .tmpl)
    echo "Generating ${dst}"
    sed -e "s/__NAMESPACE__/${NAMESPACE}/g" \
        -e "s/__APP_TOKEN__/${APP_TOKEN}/g" \
        -e "s/__ADMIN_TOKEN__/${ADMIN_TOKEN}/g" \
        -e "s/__PUBLIC_DOMAIN__/${PUBLIC_DOMAIN}/g" \
        < ${src} > ${dst}
done

echo ""
echo "Creating Namespace if needed"
echo ""
kubectl create ns ${NAMESPACE} --dry-run=client -o yaml | kubectl apply -f -

echo ""
echo "Creating/updating Secrets and ConfigMap"
echo ""

for f in ${APP_TOKEN_FILE} ${ADMIN_TOKEN_FILE} ; do
    secret=$(basename ${f} | tr '.' '-')
    descriptor=${secret}.yml
    kubectl create secret generic ${secret} --from-file=${f} --dry-run=client -o yaml > ${descriptor}
    kubectl apply -f ${descriptor} -n ${NAMESPACE} 
done

for f in axonserver.properties ; do
    cfg=$(basename ${f} | tr '.' '-')
    descriptor=${cfg}.yml
    kubectl create configmap ${cfg} --from-file=${f} --dry-run=client -o yaml > ${descriptor}
    kubectl apply -f ${descriptor} -n ${NAMESPACE} 
done
