# First Steps with AxonServer on Kubernetes

This example was made using [Docker Desktop][DockerDesktop] on MacOs. Its likely that there are some adaptions needed to run it on Windows and/or [minikube](https://minikube.sigs.k8s.io/docs). The example is based on [running-axon-server k8s-se (GitHub)](https://github.com/AxonIQ/running-axon-server/tree/master/3-k8s/1-k8s-se).

This is meant to provide basic knowledge to get started. It isn't by no means a manual for a production setup. Also see [DISCLAIMER.md](./../DISCLAIMER.md).

A reference list of commands and scripts can be found in [COMMANDS.md](./COMMANDS.md).

## Tools

### Enable Kubernetes Metrics

To get more insights on how resources are distributed in Kubernetes, [Enable Kubernetes Metrics Server on Docker Desktop](https://dev.to/docker/enable-kubernetes-metrics-server-on-docker-desktop-5434) describes how [metrics-server][metrics-server] can be installed. Here is a short summary of the steps and commands:

- If the following command results in an error message like `Metrics API not available`, the metrics server is not installed:

  ```shell
  kubectl top node
  ```

- Download `components.yaml` from the [latest release of kubernetes-sigs/metrics-server](https://github.com/kubernetes-sigs/metrics-server/releases/latest):

   ```shell
   curl -L https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml > components.yaml
   ```

- Install the metrics serer:
  
   ```shell
   kubectl apply -f components.yaml
   ```

- If there is a certificate error it may help to add `--kubelet-insecure-tls` to the args of the `metrics-server` template definition. It shouldn't be needed for newer versions though.

### Overview with Kubernetes Dashboard

A web user interface like the [Kubernetes Dashboard][KubernetesDashboard] can help to get more insights in what is going on within Kubernetes, especially for troubleshooting.
Follow the instruction in [Deploy and Access the Kubernetes Dashboard](https://kubernetes.io/docs/tasks/access-application-cluster/web-ui-dashboard) or on [github.com/kubernetes/dashboard][KubernetesDashboard] to install it. Here is a summary:

- Get latest release version number:

   ```shell
   curl --silent "https://api.github.com/repos/kubernetes/dashboard/releases/latest" | grep '"tag_name":' | sed -E 's/.*"([^"]+)".*/\1/'
   ```

- Install latest release (here v2.7.0):

   ```shell
   kubectl apply -f https://raw.githubusercontent.com/kubernetes/dashboard/v2.7.0/aio/deploy/recommended.yaml
   ```

- Create admin user:

   ```shell
   kubectl apply -f kubernetes-dashboard/dashboard-admin-user.yaml
   ```

- Create login token:

  ```shell
  kubectl -n kubernetes-dashboard create token admin-user
  ```

- Enable Access (in separate terminal):

  ```shell
  kubectl proxy
  ```

- Open the Dashboard:  
   Use the following URL and login using the previously generated token: http://localhost:8001/api/v1/namespaces/kubernetes-dashboard/services/https:kubernetes-dashboard:/proxy  

- Change the namespace in the header of the web user interface to "All namespaces" to see them all.

- If the session has expired, stop the `kubectl proxy` with Control + C and repeat the last 3 steps.

## Deploy Axon Server

Execute the scripts below to deploy [Axon Server][AxonServer].

Please look through the scripts e.g. with `cat *.sh` before you you execute them. Don't execute them if something seems suspicious for you.

- [./cleanup.sh](./cleanup.sh) deletes generated files (properties, yml, token, ssl)
- [./deploy-secrets.sh](./deploy-secrets.sh) creates the namespace `running-se` or the one given with command option `-n` and deploys `axonserver.properties`, token and certificate in Kubernetes
- [./deploy-axonserver.sh](./deploy-axonserver.sh) deploys [Axon Server][AxonServer] as a StatefulSet into the namespace `running-se` or the one given with command option `-n` in Kubernetes

### Current Status of Axon Server

Use this command to get the current status of all elements that are installed within the Kubernetes Namespace. This might take a while. Repeat the command until you see `1/1 Running`. See [Troubleshooting](#troubleshooting) if status is `0/1 Pending`.

```shell
kubectl get all --namespace running-se
```

### Create Axon Server Admin User

Axon Server access control needs to be deactivated to create the first admin user using `axonserver-cli.jar`. This is only meant to be used for local or test environments.

- Download https://download.axoniq.io/axonserver/AxonServer.zip directly or from [AxonIQ Downloads][AxonIQDownload].

- Extract the ZIP and copy `axonserver-cli.jar` into your working directory or add it to your path.

- Get the currently deployed `axonserver.properties`:

   ```shell
   kubectl get configmaps axonserver-properties --namespace running-se -o yaml > axonserver-properties-disabled-accesscontrol.yml
   ```

- Edit the file `axonserver-properties-disabled-accesscontrol.yml` and change `axoniq.axonserver.accesscontrol.enabled` to `false`.

- Apply the changed property file to Kubernetes:

   ```shell
   kubectl apply -f axonserver-properties-disabled-accesscontrol.yml --namespace running-se
   ```

- Restart Axon Server:

   ```shell
   kubectl rollout restart statefulset axonserver --namespace running-se
   ```

- Wait until Axon Server is up and running again:

   ```shell
   kubectl get all --namespace running-se
   ```

- Create the admin user:
  In this example we choose user `admin` with password `admin`. Use secure values for test- and production environments.

   ```shell
   ./axonserver-cli.jar register-user -u admin -p admin -r ADMIN -s -i
   ```

- Reenable access control again:

   ```shell
   kubectl get configmaps axonserver-properties --namespace running-se -o yaml > axonserver-properties-enabled-accesscontrol.yml
   ```
  
  Edit the file `axonserver-properties-enabled-accesscontrol.yml` and set `axoniq.axonserver.accesscontrol.enabled` back to `true`. Then apply those changes with these commands:

  ```shell
  kubectl apply -f axonserver-properties-enabled-accesscontrol.yml --namespace running-se
  kubectl rollout restart statefulset axonserver --namespace running-se
  kubectl get all --namespace running-se
  ```

### Open Web User Interface

Open the web user interface: https://localhost:8024  
You can log in using the credentials from the former step.
There might be warnings about the certificate. For the local installation they can be ignored.

### Quick Test

Please look through the script e.g. with `cat *.sh` before you you execute it. Don't execute it if something seems suspicious for you.

Run the script [./run-quicktest.sh](./run-quicktest.sh) (and optional `-n <namespace-name>`) to create an example event to try out [Axon Server's][AxonServer] Event Store.
After execution you should see the event in the [web user interface](#open-web-user-interface) under "Search" by pressing the search button without a filter.

## Troubleshooting

Here are some questions and troubles i had when i tried to reproduce the steps of the [Webinar Running Axon Server in 2020](https://youtu.be/ZJiuDGFIhPk) on YouTube.

### Where is "axonserver.yml" ?

Since this example is a bit more advanced, the YAML files are generated by the scripts based on the given templates. These are basic templates without using Helm. So `axonserver.yml` will be generated within [./deploy-axonserver.sh](./deploy-axonserver.sh) and already applied to Kubernetes. You can of course comment the last line out of the script and apply the file yourself with the following command if you want.

```shell
kubectl apply -f axonserver.yml --namespace running-se
```

### Script not executable

The scripts should already be executable. So the following command is only necessary in case they are not.

```shell
chmod +x *.sh
```

### Status pending

If the following command shows, that the pod of [Axon Server][AxonServer] is in status `pending`, open the pod in the [Kubernetes Dashboard](#overview-with-kubernetes-dashboard) and read through the events. If it is because of insufficient resources, open the [Docker Desktop][DockerDesktop], increase the resources and restart.

```shell
kubectl get all --namespace running-se
```

### Where can i find "axonserver-cli.jar"?

- Download https://download.axoniq.io/axonserver/AxonServer.zip directly or from [AxonIQ Downloads][AxonIQDownload].
- Extract the ZIP and copy `axonserver-cli.jar` into your working directory or add it to your path.

## References

- [running-axon-server (GitHub)](https://github.com/AxonIQ/running-axon-server)
- [Webinar Running Axon Server in 2020 - Bert is showing how to get the most out of AxonServer](https://youtu.be/ZJiuDGFIhPk)
- [Running Axon Server in Docker](https://developer.axoniq.io/w/running-axon-server-in-docker-continuing-from-local-developer-install-to-containerized)
- [running-axon-server-in-k8s-workshop (GitHub)](https://github.com/AxonIQ/running-axon-server-in-k8s-workshop)
- [Revisiting Axon Server in Containers](https://developer.axoniq.io/w/revisiting-axon-server-in-containers)
- [Axon Server Reference][AxonServer]
- [Get latest release version (Script)](https://gist.github.com/lukechilds/a83e1d7127b78fef38c2914c4ececc3c#gistcomment-2649739)
- [AxonIQ Downloads][AxonIQDownload]
- [Kubernetes Dashboard][KubernetesDashboard]

[AxonServer]: https://docs.axoniq.io/reference-guide/axon-server-introduction
[AxonIQDownload]: https://developer.axoniq.io/download
[DockerDesktop]: https://www.docker.com/products/docker-desktop
[metrics-server]: https://github.com/kubernetes-sigs/metrics-server
[KubernetesDashboard]: https://github.com/kubernetes/dashboard
