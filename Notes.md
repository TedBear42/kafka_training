docker run -p 2181:2181 -p 9092:9092 --env ADVERTISED_HOST=127.0.0.1 --env ADVERTISED_PORT=9092 spotify/kafka

docker ps

docker exec -it ecstatic_varahamihira bash

cd /opt/kafka_2.11-0.10.1.0/bin

./kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 2 --topic training

./kafka-topics.sh --list --zookeeper localhost:2181

./kafka-console-producer.sh --broker-list localhost:9092 --topic training

./kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic training --from-beginning

./kafka-consumer-groups.sh --zookeeper localhost:2181 --list

./kafka-consumer-groups.sh --zookeeper localhost:2181 --describe --group test-consumer-group