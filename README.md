
# Running a Solo Kafka Contain Hello World example
After you finish the followig steps will will have successfully

- Installed and started a Kafka single node cluster on your computer.  This will include one ZooKeeper and one Kafka Broker
- Create one Kafka Topic
- Create one Kafka Producer
- Create one Kafka Consumer
- Send Messages from the Kafka Producer to the Kafka Consumer

## Setting up Kafka Docker Container
The following line will start a kafka docker container on your local
```
docker run \
-p 2181:2181 \
-p 9092:9092 \
--env ADVERTISED_HOST=127.0.0.1 \
--env ADVERTISED_PORT=9092 \
--name mykafka \
--detach \
spotify/kafka
```

The following line will list out of the docker containers you have running on your local
```
docker ps
```

You should see something like the following
```
   CONTAINER ID        IMAGE               COMMAND             CREATED             STATUS              PORTS                                            NAMES
   a7b9cf39eb05        spotify/kafka       "supervisord -n"    39 seconds ago      Up 38 seconds       0.0.0.0:2181->2181/tcp, 0.0.0.0:9092->9092/tcp   mykafka
```

## Create a Topic
The following commands will get you into the container using bash and there we will create and list topics
```
docker exec -it mykafka bash

cd /opt/kafka_2.11-0.10.1.0/bin

./kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 2 --topic training

./kafka-topics.sh --list --zookeeper localhost:2181
```
## Produce to a Topic and Consume it
In the same cmd terminal use the following commands to set up a producer
```
./kafka-console-producer.sh --broker-list localhost:9092 --topic training
```

Then open up a new cmd terminal and execute the following commands to set up a consumer
```
docker exec -it mykafka bash

cd /opt/kafka_2.11-0.10.1.0/bin

./kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic training --from-beginning
```

Then in the producer window type in some random text and press enter and repeat a couple of times.  
Then look at the output in the consumer terminal.

# Now produce and consume from you local Kafka from a Java Application
The following steps will show how you can interact with you newly created Kafka container with a Java application running on your local.
After you finish these steps you will have finished the following:

- Build a Java application with Maven with Kafka dependencies
- Execute a Java application with a production to Kafka
- Execute a Java application will a consumer from Kafka
- See how the following api codes work
-- Production Listener
-- Consumer Listener 

## Build the Java Code
First we need to build the project.  To do this just go into this director with a terminal and execute the following command
```
mvn package
```

If you are successful you should see jars in a newly created directory called **target**

## End the Java Code
Now we want to execute our java applications.  First we will execute the consumer example with the following line
```
java -cp target/KafkaTraining.jar com.malaska.kafka.training.ConsumerExeample 127.0.0.1 9092 training
```
Then in a different terminal we will execute the producer with the following line
```
java -cp target/KafkaTraining.jar com.malaska.kafka.training.ProducerExample 127.0.0.1 9092 training
```

Now look at the consumer terminal to see the output

#Extra notes

## Checking out the consumer group
./kafka-consumer-groups.sh --zookeeper localhost:2181 --list

./kafka-consumer-groups.sh --zookeeper localhost:2181 --describe --group test-consumer-group
