#!/bin/bash

# Build gateway
cd apiGateway
mvn clean install package

cd ../serviceRegistry
mvn clean install package

cd ../restfulService
mvn clean install package