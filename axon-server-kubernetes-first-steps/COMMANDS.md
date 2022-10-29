# Commands and Scripts

These commands are meant to be used to make the first steps with [Axon Server][AxonServer] on a local Kubernetes. These are meant to provide basic knowledge to get started, they aren't by no means a manual for a production setup. Also see [DISCLAIMER.md](./../DISCLAIMER.md).

Please look through scripts e.g. with `cat *.sh` before you you execute them. Don't execute them if something seems suspicious for you.

## Create a Kubernetes Namespace (optional)

The default namespace name for this example is `running-se`.
If you choose another one be sure to add the `-n` command line option for [deploy-secrets.sh](./deploy-secrets.sh) and [./deploy-axonserver.sh](deploy-axonserver.sh) below. It will automatically be created if missing by [deploy-secrets.sh](./deploy-secrets.sh). This command is therefore optional.

```shell
kubectl create namespace running-se
```

## Allow Script Execution (optional)

The scripts should already be executable. So the following command is only necessary in case they are not.

```shell
chmod +x *.sh
```

## Clean up generated files

Run the script [./cleanup.sh](./cleanup.sh) to delete generated files (properties, yml, token, ssl).

## Deploy Properties and Secrets

Run the script [./deploy-secrets.sh](./deploy-secrets.sh) to deploy `axonserver.properties`, tokens and the TLS (SSL) certificate in Kubernetes.
This command will create the namespace `running-se` or the one given with command option `-n` if it doesn't exist yet.

## Deploy Axon Serer

Run the script [./deploy-axonserver.sh](./deploy-axonserver.sh) to generate `axonserver.yml` and deploy [Axon Server][AxonServer] as a StatefulSet in Kubernetes.
This command will use the namespace `running-se` by default or the one given with command option `-n`.

## Get the current status of the namespace

```shell
kubectl get all --namespace running-se
```

## Quick test

Run the script [./run-quicktest.sh](./run-quicktest.sh) (and optional `-n <namespace-name>`) to create an example event to try out [Axon Server's][AxonServer] Event Store.

## Open Web User Interface

Open the web user interface: https://localhost:8024 .
There might be warnings about the certificate. For the local installation they can be ignored.

## Create an admin user

```shell
./axonserver-cli.jar register-user -u admin -p admin -r ADMIN -s -i
```

## Update Axon Server

```shell
kubectl apply --filename axonserver.yml --namespace running-se
```

## Restart Axon Server

```shell
kubectl rollout restart statefulset axonserver --namespace running-se
```

## Delete Axon Server

```shell
kubectl delete --filename axonserver.yml --namespace running-se
```

## Delete the whole namespace

This will take some time and will clean up the whole namespace including Axon Server, all volume mount claims, secrets,... . After that you'll need repeat all steps that are needed to create a new namespace and deploy [Axon Server][AxonServer] again. This might be helpful if you want to start all over with a clean setup.

```shell
kubectl delete namespaces running-se
```

[AxonServer]: https://docs.axoniq.io/reference-guide/axon-server-introduction