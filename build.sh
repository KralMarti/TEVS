#!/bin/bash

# Build gateway
cd apiGateway
mvn clean install package -DskipTests

cd ../serviceRegistry
mvn clean install package -DskipTests

cd ../restfulService
mvn clean install package -DskipTests
