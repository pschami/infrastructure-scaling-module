[![License](https://img.shields.io/badge/License-EPL%201.0-red.svg)](https://opensource.org/licenses/EPL-1.0)
[![Build Status](https://travis-ci.org/dellemc-symphony/infrastructure-scaling-module.svg?branch=master)](https://travis-ci.org/dellemc-symphony/infrastructure-scaling-module)
# infrastructure-scaling-module
## Description
This repository demonstrates how Project Symphony can be used to evaluate and provide intelligent responses to infrastructure changes. It absorbs alerts and decides whether to add additional nodes to a Dell EMC Converged System such as a VxRack FLEX or VxBlock System, both of which can be scaled for aggregate memory and CPU capacity by adding cluster nodes. 

## Documentation
You can find additional documentation for Project Symphony at [dellemc-symphony.readthedocs.io][documentation].

## Before you begin
Before using this module, download or build the following Project Symphony containers (git clone, mvn install):  
* cpsd-core-capability-registry-service
* cpsd-rackhd-adapter-service
* cpsd-vcenter-adapter-service
* cpsd-core-endpoint-registry-service
* cpsd-coprhd-adapter-service  

Verify that the following tools are installed:

* Java Development Kit (version 8)
* Apache Maven 3.0.5+ (including ~/.m2/settings.xml providing relevant repositories)
* Docker daemon
* RabbitMQ  3.6.6

**Note:** Maven currently relies on internal Dell EMC repositories for build artifacts.
## Building
Run the following command:  
  
```  
mvn install
```  

The code is packaged in a JAR file inside a Docker container. 
## Deploying
The Docker image needs to connect to a RabbitMQ service on port 5672 and to a capability registry.

## Contributing
Project Symphony is a collection of services and libraries housed at [GitHub][github].

Contribute code and make submissions at the relevant GitHub repository level. See [our documentation][contributing] for details on how to contribute.

## Community
Reach out to us on the Slack [#symphony][slack] channel by requesting an invite at [{code}Community][codecommunity].

You can also join [Google Groups][googlegroups] and start a discussion.

[documentation]: https://dellemc-symphony.readthedocs.io/en/latest/
[slack]: https://codecommunity.slack.com/messages/symphony
[googlegroups]: https://groups.google.com/forum/#!forum/dellemc-symphony
[codecommunity]: http://community.codedellemc.com/
[contributing]: http://dellemc-symphony.readthedocs.io/en/latest/contributingtosymphony.html
[github]: https://github.com/dellemc-symphony
