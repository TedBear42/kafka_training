package com.malaska.kafka.training


import java.util
import java.util.{Collections, Properties}

import net.liftweb.json.DefaultFormats
import net.liftweb.json.Serialization.read
import org.apache.kafka.clients.consumer.{ConsumerConfig, KafkaConsumer}

import scala.collection.mutable

object LatencyConsumerExample {
  def main(args:Array[String]): Unit = {
    val kafkaServerURL = args(0)
    val kafkaServerPort = args(1)
    val topic = args(2)

    println("Setting up parameters")
    val props = new Properties()
    props.put("bootstrap.servers", kafkaServerURL + ":" + kafkaServerPort)
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "TrainingConsumer")
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")
    props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000")
    props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000")
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")


    println("Creating Consumer")
    val consumer = new KafkaConsumer[String,String](props)
    consumer.subscribe(Collections.singletonList(topic))

    implicit val formats = DefaultFormats

    var maxLatency = 0l
    var minLatency = 100000l
    var latencyN = 0f
    var latencyCount = 0l
    val lastNLatencies = new mutable.MutableList[Long]

    println("Starting Consumer")
    while (true) {
      val records = consumer.poll(1000)
      val it = records.iterator()
      while (it.hasNext) {
        val record = it.next()
        val exampleMessage = read[ExampleMessage](record.value())
        val latency = System.currentTimeMillis() - exampleMessage.sentTime
        maxLatency = Math.max(latency, maxLatency)
        minLatency = Math.min(latency, minLatency)
        latencyN += latency
        latencyCount += 1
        lastNLatencies += latency


        if (latencyCount % 10 == 0) {
          println("MessageCount:" + latencyCount +
            ",MaxLatency:" + maxLatency +
            ",MinLatency:" + minLatency +
            ",AverageLatency:" + (latencyN/latencyCount) +
            ",LastN:(" + lastNLatencies.mkString(",") + ")")
          lastNLatencies.clear()
        }
      }
    }
  }
}

