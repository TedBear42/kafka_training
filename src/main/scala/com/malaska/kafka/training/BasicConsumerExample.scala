package com.malaska.kafka.training

import java.util
import java.util.{Collections, Properties}

import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRebalanceListener, ConsumerRecord, KafkaConsumer}
import org.apache.kafka.common.TopicPartition

/**
  * Created by tmalaska on 6/24/17.
  */
object BasicConsumerExample {
  def main(args:Array[String]): Unit = {
    val kafkaServerURL = args(0)
    val kafkaServerPort = args(1)
    val topic = args(2)

    println("Setting up parameters")
    val props = new Properties()
    props.put("bootstrap.servers", kafkaServerURL + ":" + kafkaServerPort)
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "TrainingConsumer");
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
    props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
    props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

    println("Creating Consumer")
    val consumer = new KafkaConsumer[String,String](props)

    val listener = new RebalanceListener

    consumer.subscribe(Collections.singletonList(topic), listener)


    println("Starting Consumer")
    while (true) {
      val records = consumer.poll(1000)
      val it = records.iterator()
      while (it.hasNext) {
        val record = it.next()
        println("Received message: (" + record.key() + ", " + record.value() + ") at offset " + record.offset())
      }
    }
  }
}

class RebalanceListener extends ConsumerRebalanceListener {
  override def onPartitionsAssigned(collection: util.Collection[TopicPartition]): Unit = {
    print("Assigned Partitions:")
    val it = collection.iterator()
    while (it.hasNext) {
      print(it.next().partition() + ",")
    }
    println
  }

  override def onPartitionsRevoked(collection: util.Collection[TopicPartition]): Unit = {
    print("Revoked Partitions:")
    val it = collection.iterator()
    while (it.hasNext) {
      print(it.next().partition() + ",")
    }
    println
  }
}
