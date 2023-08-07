# API definition can be found at: 
### http://localhost:9090/swagger-ui.html

### K8s folder has the deployment and service yaml files for the application
Redis Cache can be enabled by running the following commands
> ```docker-compose -f docker-compose-redis-only.yml up -d```
> 
> ```docker exec -it k8s-redis-1 /bin/bash```

Kubernetes Secrets can be enabled by running the following commands
> ```kubectl apply -f secret.yaml```
 
MySql can be enabled by running the following commands
> ```kubectl apply -f mysql-pv.yaml```
> 
> ```kubectl apply -f mysql-pvc.yaml```
>
> ```kubectl apply -f mysql-deployment.yaml```
> 
> ```kubectl apply -f mysql-service.yaml```
 
Finally, run the following command to forward the port
> ```kubectl port-forward mysql-6b95f69c9f-nkklm 3306:3306```

### "X-Trace-Id" is added to the response header for tracing purpose

### Active profiles has to be set to "dev" for running the application locally

### Dockerfile is added to the root folder for building the docker image

### Cache is implemented via @Cacheable annotation

### Actuator is enabled for health check and metrics