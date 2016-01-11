GR8 Event-Driven Microservices
=======

These projects will demo how to pub/sub to both RabbitMQ and Kafka.

Setup
====

Install RabbitMQ
---

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

Boot Consumer
---
Simply `gradle bootRun` from the consumer project.  The `application.yml` is currently configured to start the server on port 8282.

Trigger Producer
---
To put a message onto the RabbitMQ and/or the Kafka topic simply use your favorite REST client tool like

```bash
curl -X POST -H "Content-type: application/json" --data '{"name": "Christian"}' http://localhost:8080/person
```