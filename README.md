[![License](https://img.shields.io/badge/License-EPL%201.0-red.svg)](https://opensource.org/licenses/EPL-1.0)
# infrastructure-scaling-module
## Description
This module demonstrates how Symphony can be used to evaluate changes in the infrastructure and provide intelligence responses.  In this case the module provides a simplistic state machine which absorbs alerts and decides whether to add additional nodes to a vSphere-based product like VXrack or VXblock both of which can be scaled in terms of aggregate memory and CPU capacity by adding cluster nodes. 

## Documentation
## API overview
## Before you begin

Note, you will need to either download or build the following other Symphony containers before using this demo (git clone, mvn install):  
* cpsd-core-capability-registry-service
* cpsd-rackhd-adapter-service
* cpsd-vcenter-adapter-service
* cpsd-core-endpoint-registry-service
* cpsd-coprhd-adapter-service  

## Building
To compile the code and then create a Docker image you will need:
* Java 8+ SDK
* maven (including ~/.m2/settings.xml providing relevant repos)
* docker daemon
(note that currently maven relies on internal DellEMC repositories for build artifacts)  
  
```
mvn compile -U clean  
mvn install
```  

## Packaging
The code is packaged in a jar file inside a Docker container.  
## Deploying
The docker image will need to connect to a RabbitMQ service on 5672 and to a capability registry.


## Deploying
## Contributing
## Community
