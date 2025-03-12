# Project 3b

## K8s 

Kubernetes is a _Container orchestration system_.

**Docker** wraps applications into boxes with all what is needed for the applications to run.
The primary purpose of
a container is to define and enforce
boundaries around specific resources
and to provide separation of concerns.

**Kubernetes** manages the boxes.

Advantages of using k8s:
- **Resilience**: Ensures that applications continue running even if some containers or nodes fail. Kubernetes automatically restarts failed containers and reschedules workloads.
- **Scalability**: Supports horizontal scaling (adding more instances of an application) and high availability, ensuring applications can handle increasing workloads efficiently.
- **Self-healing**: Detects and replaces failed containers automatically, reschedules pods if a node goes down, and restarts unresponsive applications.
- **Versioning**: Enables automated rollouts and rollbacks, ensuring seamless updates and the ability to revert changes if needed.
- **Service discovery and load balancing**:  Kubernetes automatically assigns DNS names to services and distributes traffic evenly across available pods to optimize performance. 
- **Security**: Provides built-in secret management and configuration management, ensuring sensitive data (e.g., API keys, passwords) is securely handled without exposing them in application code.

A Kubernetes cluster consists of:
- **Master Node** (Control Plane) – manages the cluster and schedules applications.
- **Worker Nodes** – run the containerized applications.

### Types of resources in K8s
#### Pods
- The smallest and simplest object managed by Kubernetes.
- Each pod contains one or more containers that share resources and networking.
- Pods are ephemeral resources.
- Pods are created and destroyed to match the desired state of your cluster as specified in Deployments .

A Pod represents a single instance of a running process
in a cluster. Each pod runs
one or more containers (usually Docker containers)
and provides an environment
for them to share resources (storage,
networking, and
configurations).
A Pod Has a unique IP address
inside the cluster.
Can be scheduled on any node
in the Kubernetes cluster.
Can restart containers
if they fail
(but does not self-heal;
Deployments handle that).

**A Pod contains**:

- **Containers**: The actual applications/processes running inside the Pod.

- **Storage (Volumes)**: Shared storage for all containers in the Pod.

- **Network**: Each Pod gets its own IP address, and containers inside the Pod can communicate via localhost.

- **Configuration**: Environment variables, secrets, and **ConfigMaps**.


A Pod goes through
the following **phases**:
-   **Pending	Pod** is created
    but waiting for resources.
-   **Running Pod** is up and running with all containers.
-   **Succeeded Pod** has completed execution (for jobs, batch tasks).
-   **Failed**	One or more containers in the Pod failed.
-   **Unknown**	The Pod state cannot be determined (possible network issue).

```
kubectl get pods
kubectl describe pod my-pod
kubectl logs my-pod
```

#### Services
- Provide a stable access point to a set of Pods, even if the underlying infrastructure changes.
- The set of Pods targeted by a Service is usually determined by a selector that you define.
- Service types:
  - **ClusterIP** (default) – accessible only within the cluster. This is the default that is used if you don't explicitly specify a type for a Service. You can expose the Service to the public internet using an Ingress.
  
    Example: A backend service that should only be accessible by frontend pods.
  - **NodePort** – exposes the application on a fixed port (static) on each node.
    
    Example: Local Kubernetes, Accessing a service on http://\<NodeIP>:\<NodePort>
  - **LoadBalancer** – integrates with cloud load balancers for external access.
    
    Example: Production workloads on cloud providers.

  - **Headless (None)**  -  Directly access to pods via DNS. Does not assign a virtual IP.
    
  - Example: A Redis or MySQL cluster where clients need direct access to individual pods.

To sum up:
- Every Pod gets its own IP inside the cluster.
- Containers in the same Pod can communicate via localhost.
- Pods talk to each other using Services (e.g., ClusterIP, NodePort, LoadBalancer).

#### ConfigMaps & Secrets
- ConfigMap – stores configuration settings such as environment variables and config files.
- Secrets – securely store sensitive data like API keys and credentials.

#### Deployments & StatefulSets
- Deployments – used for stateless applications (do not store data between restarts).

- A Deployment in Kubernetes manages the scaling, updating, and rollback
  of application pods. It ensures the desired number
  of replicas are running and automatically replaces failed pods.

- StatefulSets – used for stateful applications, such as databases (Redis, MySQL).

StatefulSets in Kubernetes  are used to manage stateful
applications that require stable network identities,
persistent storage, and ordered deployments.
Unlike Deployments, StatefulSets ensure that each pod gets a unique, stable hostname and persistent storage across restarts.



## Design patterns

### Sidecar Pattern

The Sidecar pattern 
involves running multiple containers 
within the same Pod, 
where the main container 
handles the primary application logic, 
while sidecar containers 
provide auxiliary services. 
These supporting containers 
can manage tasks like logging, 
monitoring, proxying, 
or security without 
modifying the main application.

For example, a Node.js 
application container 
could have a sidecar 
container dedicated 
to collecting logs and 
forwarding them to an 
external logging system. 

This approach improves modularity, 
reusability, 
and maintainability 
by separating concerns.


### Ambassador Pattern

The Ambassador pattern 
uses a secondary container 
as a proxy between 
the main application 
container and external services. 
This pattern helps manage 
outbound traffic, 
enabling functionalities 
like request routing, 
load balancing, 
retries, and failover handling.

For example, an Ambassador container can intercept API requests from the primary application and forward them to different backend services based on predefined rules. This enhances flexibility, resilience, and maintainability by offloading networking concerns from the main application.
A Spring Boot microservice could have an Envoy proxy as an ambassador, managing all outbound traffic to backend services


## Caching

### Redis
Caching is technique to store frequently accessed data in an in-memory data store (Redis), to improve performance 
 and reduce database load. 

**Cache prefetching**: 
An entire product catalog can be pre-cached in Redis, and the application can perform any product query on Redis similar to the primary database.

**Cache-aside pattern**: Redis is filled on demand, based on whatever search parameters are requested by the frontend.

Steps taken in the cache-aside pattern 
when there is a "cache miss.":

1. An application requests data from the backend.
2. The backend checks to find out if the data is available in Redis.
3. Data is not found (a cache miss), so the data is fetched from the database.
4. The data returned from the database is subsequently stored in Redis.
5. The data is then returned to the application.

Search also for
Caching Patterns for Read 
- Cache-Aside 
- Read-Through
Caching Patterns for Write
- Write-Through 
- Write-Back 
- Write-Around

### Envoy 

Envoy is a high-performance, cloud-native proxy designed for service-to-service communication in modern distributed architectures like Kubernetes and microservices.
Key Features of Envoy
- Service Discovery & Load Balancing: Dynamically
routes requests to healthy services.
- Observability: Provides metrics, logs, and tracing for better debugging.
- Security: Supports TLS termination, authentication, and authorization.
- Traffic Management: Enables retries, circuit breaking, and rate limiting.

## Demo

#### Step 1 
Choose the K8s environment for testing.

Install minikube https://minikube.sigs.k8s.io/docs/
and run:

```
minikube start

minikube dashboard
```
#### Step 2
Connect to dockerhub and build 
a docker image for the Spring Boot App. 

```
docker build -t awbd2021/lab3b .
```

```
docker push awbd2021/lab3b
```

#### Step 3
Create a StefulSet for redis:

```
kubectl create -f redis-shards.yaml
```

Kubernetes will create pods with stable names, e.g., sharded-redis-0, sharded-redis-1, sharded-redis-2.
and ensures that the StatefulSet 
manages pods with the label app: redis.

_template_: Defines how each Redis pod should be configured.

#### Step 4 

Create a service for redis shards:

```
kubectl create -f redis-service.yaml
```

The service will provide network access to a set of pods.
It creates DNS records for each pod in the StatefulSet.

```
kubectl get svc
```
A headless Service allows a client to connect to whichever Pod it prefers, directly. Services that are headless don't configure routes and packet forwarding using virtual IP addresses and proxies; instead, headless Services report the endpoint IP addresses of the individual pods via internal DNS records, served through the cluster's DNS service.

```
kubectl get pods -l app=redis 
```


#### Step 5

Create the configuration for envoy proxy.
This will serve the envoy deployment.

The DNS format for services and pods usually follows:

<pod-name>.<service-name>.<namespace>
pod-name - sharded-redis-1
service-name - redis

Envoy will send requests to:
```
socket_address:
    address: sharded-redis-1.redis
    port_value: 6379
```

```
kubectl create configmap envoy-config --from-file=envoy.yaml
```

#### Step 6

Define the deployment to create the envoy pod and the envoy service.

```
kubectl create -f envoy-deployment.yaml
```

```
kubectl create -f envoy-service.yaml
```

#### Step 7
Create the springboot app pods.

```
kubectl create -f springbootapp-deployment.yaml
```

```
kubectl create -f springbootapp-service.yaml
```

#### Step 7
Expose the app via minikube tunnel.

minikube tunnel is a command 
that allows LoadBalancer-type 
services in a Minikube cluster 
to be accessible from local machine 
by creating a network route.

In a real K8s cluster (in a cloud environment), 
a LoadBalancer service gets an external 
IP from a cloud provider (AWS, GCP, Azure).
In Minikube, there’s no cloud provider 
to assign an external IP, 
so minikube tunnel acts as a workaround by:
- Creating a network route to access LoadBalancer services. 
- Assigning a real external IP to the service.

Run 
```
minikube service image-api
```

#### Step 9
Test requests with Postman:

POST
http://127.0.0.1:62153/images/1

GET
http://127.0.0.1:62153/images/1

### References

[1] https://kubernetes.io/docs/concepts/

[2] https://redis.io/learn/howtos/solutions/microservices/caching

[3] https://kubernetes.io/docs/tasks/access-application-cluster/ingress-minikube/
