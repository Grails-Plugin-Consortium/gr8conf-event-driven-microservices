GR8 Event-Driven Microservices
=======

These projects will demo how to pub/sub to both RabbitMQ and Kafka.

Setup Using Docker
=====

Install Docker
-----
Install [Docker Toolbox](https://docs.docker.com/mac/step_one/) and get all that stuff configured.

You will need to make sure to have a default docker machine created via `docker-machine create --driver=virtualbox default`.  In your shell make sure to run `eval $(docker-machine env default)`

Running Application
----
Bundled in source is a simply shell script named `build-run.sh` located in the docker directory.  Simply `cd` into the `docker` directory and run `./build-run.sh`

This will compile the apps, create the container and images and then start all the things.

You can then get the IP of the running instance via `docker-machine ip default`.  Note the IP and replace localhost with the docker IP in the #Testing All The Things# section.  Proceed to the #Testing All The Things# to test the setup. 

To see the data in action you may need to tail the logs with a docker logs on the docker_producer and docker_consumer images like the following `docker logs -f db18a7ed796e`.  To get the image names do a `docker ps` command.

Setup Running Directly
=====

Install RabbitMQ
---
Install via instruction at the [rabbitmq website](https://www.rabbitmq.com/install-standalone-mac.html)

Install Kafka
---
These are similar to the directions at [kafka quickstart](http://kafka.apache.org/documentation.html#quickstart)

_This can also be done from a docker container if you want to go down that route, but these docs will not cover that in detail. Details [here](https://github.com/wurstmeister/kafka-docker)_

1. Download [kafka](https://www.apache.org/dyn/closer.cgi?path=/kafka/0.9.0.0/kafka_2.11-0.9.0.0.tgz)
2. Unzip kafka and cd into the directory
3. Run Zookeeper using `bin/zookeeper-server-start.sh config/zookeeper.properties`
4. In new terminal start Karka `bin/kafka-server-start.sh config/server.properties`
5. In new terminal Create `person` topic `bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic person`
6. _Optional:_ If you want another topic subscriber to see messages use new console `bin/kafka-console-consumer.sh --zookeeper localhost:2181 --from-beginning --topic person`

Boot Producer
----
Simply `gradle bootRun` from the producer project.   The `application.yml` is currently configured to start the server on port 8080.

In the configuration you can use the following to control data.

``` yaml
---
# Enable or disable posting messages to each
rabbitmq.enabled: true
kafka.enabled: true

---
# Kafka configuration
brokerList: localhost:9092
sync: async         # use async or sync post to producer
topic: person
```

Boot Consumer
---
Simply `gradle bootRun` from the consumer project.  The `application.yml` is currently configured to start the server on port 8282.



Testing All The Things
======

Trigger Producer
---
To put a message onto the RabbitMQ and/or the Kafka topic simply use your favorite REST client tool like

```bash
curl -X POST -H "Content-type: application/json" --data '{"name": "Christian"}' http://localhost:8080/person
```

Notes
---
You may see a `java.lang.RuntimeException: Reloading agent exited via exception, please raise a jira` exception during the first post to kafka when running in `run-app` or `bootRun` if using async.  This can be ignored and appears to not cause any data issues.
