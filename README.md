# API definition can be found at: 
### http://localhost:9090/swagger-ui.html

### K8s folder has the deployment and service yaml files for the application

### "X-Trace-Id" is added to the response header for tracing purpose

### Active profiles has to be set to "dev" for running the application locally

### Dockerfile is added to the root folder for building the docker image

### Cache is implemented via @Cacheable annotation

### Actuator is enabled for health check and metrics