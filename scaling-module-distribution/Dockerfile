#
# Copyright (c) 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
# Dell EMC Confidential/Proprietary Information
#
#  USAGE: (from .. dir)
#    docker build -f scaling-module-distribution/Dockerfile --tag=scaling-module scaling-module
#

# Start from base image with Java 8
FROM java:8-jre-alpine

RUN mkdir -p /opt/dell/cpsd/scaling-module/bin
RUN mkdir -p /opt/dell/cpsd/scaling-module/logs
RUN mkdir -p /opt/dell/cpsd/scaling-module/lib
RUN mkdir -p /opt/dell/cpsd/scaling-module/install

COPY target/dependency/lib/scaling-module-*.jar /opt/dell/cpsd/scaling-module/lib/scaling-module.jar
COPY docker/image/bin/run.sh /opt/dell/cpsd/scaling-module/bin/

RUN chmod 755 /opt/dell/cpsd/scaling-module/bin/run.sh

EXPOSE 8080

CMD ["/opt/dell/cpsd/scaling-module/bin/run.sh"]
