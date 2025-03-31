# Project 4b

## Monitoring application metrics 

### Helm

Helm is a package manager for Kubernetes that 
facilitates deployment and managing prepackaged 
applications like Kafka. It is especially useful 
when application are made up of multiple K8s objects.

Applications are packaged as "charts" with
necessary configuration files and other 
setting options.

_Helm client_ is used to:
-  manage charts locally, manage local repositories.
-  interact with Helm repositories.
-  install applications in K8s.

Behind the scenes _Helm Library_ combines 
Helm templates with user configurations to
create applications. Helm Library communicates with K8s cluster.

Chart components:
- **Chart.yaml**: char metadata like name, version, description and dependencies on other charts.
- **templates**: define k8s resources.
- **values.yaml** + values.schema.json: default configuration settings 
that can be overridden when the application is installed in K8s. 

### Prometheus

Prometheus [3] is an open-source systems monitoring and alerting toolkit.
Prometheus collects and stores its metrics 
as time series data, 
i.e. metrics information is stored 
with the timestamp at which 
it was recorded, alongside 
optional key-value pairs called labels.

Metrics play an important role
in understanding why your
application is working in a
certain way.

The term time series refers 
to the recording of changes 
over time. What users want 
to measure differs from 
application to application. 
For a web server, it could be 
request times; for a database, 
it could be the number of active 
connections or active queries, 
and so on.

Prometheus scrapes metrics from 
instrumented jobs, either directly 
or via an intermediary push gateway 
for short-lived jobs. It stores all 
scraped samples locally and runs 
rules over this data to either 
aggregate and record new time 
series from existing data or 
generate alerts. 

Grafana or other API consumers 
can be used to visualize 
the collected data.

## Demo

#### Step 1 
Check requirements and install Helm [1].

#### Step 2

Add Prometheus chart to helm repository:

```
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
```
We can check the list of available charts:

```
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
```

#### Step 3
Install prometheus chart with the default settings.

```
helm install prometheus prometheus-community/prometheus
```
Check in k8s dashboard 
the resources added by helm. 

You should find a config map 
_prometheus-server_.

```
kubectl get configmap
```



#### Step 4 

Check that actuator and micrometer-registry-prometheus
are added in the list of maven dependencies.

**Actuator**  provides production-ready features 
for Spring Boot applications. It enables monitoring and management 
applications  
by exposing various HTTP endpoints 
that provide information about 
the application's health, metrics, environment etc.

**Micrometer** exports application metrics 
in a format that Prometheus 
can scrape. The actuator exposes the metrics, and the prometheus registry makes those metrics available to the prometheus monitoring system.

```
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
	<groupId>io.micrometer</groupId>
	<artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

To expose actuator endpoint add in application.properties:

```
management.endpoints.web.exposure.include=prometheus
```
#### Step 5

Build the app and push it to your docker repository:

```
docker build -t ***/lab3b .
```

```
docker push ***/lab3b
```


#### Step 6

In K8s dashboard modify the ConfigMap: prometheus-server
by adding a scrape_config with job_name: 'spring-boot-app'

```
prometheus.yml: |
...
	- job_name: 'spring-boot-app'
	  metrics_path: '/actuator/prometheus'
	  kubernetes_sd_configs:
	    - role: pod
	  relabel_configs:
		- source_labels: [__meta_kubernetes_pod_label_app]
		  action: keep
		  regex: image-api
```

This configuration tells Prometheus server to:
- Discover pods in the Kubernetes cluster (kubernetes_sd_configs).
- Filter those pods, keeping only the ones that have the label app: image-api.
- Scrape metrics from the /actuator/prometheus endpoint of the remaining pods.

#### Step 7

Use minikube tunnelling to expose prometheus-server dashboard.

```
minikube service prometheus-server
```

```
http://127.0.0.1:port/targets
```

You should be able to monitor image-api apps.

#### Step 8

Modify the StatefulSet sharded-redis by adding a sidecar container
that will export redis shard metrics in
prometheus format. Each pod will run a redis shard container
and a redis-exporter container.

```
- name: redis-exporter
    image: oliver006/redis_exporter:latest
    ports:
        - containerPort: 9121
    args:
        - "--redis.addr=redis://localhost:6379"
```

#### Step 9

In K8s dashboard modify the ConfigMap: prometheus-server
by adding a scrape_config with job_name: 'redis'

```
- job_name: 'redis'
  kubernetes_sd_configs:
  - role: pod
  relabel_configs:
  - source_labels: [__meta_kubernetes_pod_container_name]
  action: keep
  regex: redis-exporter
```
    

### References

[1] https://helm.sh/docs/topics/kubernetes_distros/

[2] https://prometheus-community.github.io/helm-charts/
 
[3] https://prometheus.io/docs/introduction/overview/

