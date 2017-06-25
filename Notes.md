
## Setting up Kafka Docker Container
docker run -p 2181:2181 -p 9092:9092 --env ADVERTISED_HOST=127.0.0.1 --env ADVERTISED_PORT=9092 spotify/kafka

docker ps

## Create a Topic
docker exec -it ecstatic_varahamihira bash

cd /opt/kafka_2.11-0.10.1.0/bin

./kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 2 --topic training

./kafka-topics.sh --list --zookeeper localhost:2181

## Produce to a Topic and Consume it
./kafka-console-producer.sh --broker-list localhost:9092 --topic training

./kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic training --from-beginning

## Checking out the consumer group
./kafka-consumer-groups.sh --zookeeper localhost:2181 --list

./kafka-consumer-groups.sh --zookeeper localhost:2181 --describe --group test-consumer-group

## Build the Java Code
mvn package

## End the Java Code

java -cp target/KafkaTraining.jar com.malaska.kafka.training.ConsumerExeample 127.0.0.1 9092 training

java -cp target/KafkaTraining.jar com.malaska.kafka.training.ProducerExample 127.0.0.1 9092 training