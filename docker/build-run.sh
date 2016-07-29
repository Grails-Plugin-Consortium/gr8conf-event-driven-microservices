#!/usr/bin/env bash

RELEASE_VERSION=${1-snapshot}
IP=${2-}
# Add a colon if IP is set, we want <host_ip>:<host_port:<container_port> or <host_port:<container_port>
if [ -n "$IP" ]
then
IP=${IP}:
fi

echo "================== Config ======================="
echo " RELEASE_VERSION: $RELEASE_VERSION"
echo " IP: $IP"
echo "================================================="

SCRIPT_DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )

# Create Dockerfile for consumer and producer
sed "s|{RELEASE_VERSION}|$RELEASE_VERSION|g;" $SCRIPT_DIR/consumer/Dockerfile.template > $SCRIPT_DIR/consumer/Dockerfile
sed "s|{RELEASE_VERSION}|$RELEASE_VERSION|g;" $SCRIPT_DIR/producer/Dockerfile.template > $SCRIPT_DIR/producer/Dockerfile
sed "s|{RELEASE_VERSION}|$RELEASE_VERSION|g;" $SCRIPT_DIR/stream/Dockerfile.template > $SCRIPT_DIR/stream/Dockerfile

cd ..

./gradlew :gr8-rest-producer:clean :gr8-rest-producer:assemble
cp -rf gr8-rest-producer/build/libs/gr8-rest-producer-0.1.jar docker/producer
./gradlew :gr8-rest-consumer:clean :gr8-rest-consumer:assemble
cp -rf gr8-rest-consumer/build/libs/gr8-rest-consumer-0.1.jar docker/consumer
./gradlew :gr8-rest-stream-producer:clean :gr8-rest-stream-producer:assemble
cp -rf gr8-rest-stream-producer/build/libs/gr8-rest-stream-producer-0.1.jar docker/stream

cd docker

#Optional if already running
docker-machine start

eval `docker-machine env default`

# Cleanup any existing containers and delete their volumes.
docker-compose -f $SCRIPT_DIR/docker-compose.yml kill
docker-compose -f $SCRIPT_DIR/docker-compose.yml rm --force  # Not using -v flag to preserve volumes between deploys

# create and run all containers
docker-compose -f $SCRIPT_DIR/docker-compose.yml build
docker-compose -f $SCRIPT_DIR/docker-compose.yml up -d

# Scale kafka to 3
docker-compose -f $SCRIPT_DIR/docker-compose.yml scale kafka=3
