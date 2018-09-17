package com.malaska.kafka.training

import java.util
import java.util.Properties

import org.apache.kafka.clients.producer._
import org.apache.kafka.common.Cluster

import net.liftweb.json.DefaultFormats
import net.liftweb.json.Serialization.write

object LatencyProducerExample {

  def main(args:Array[String]): Unit = {
    val kafkaServerURL = args(0)
    val kafkaServerPort = args(1)
    val topic = args(2)
    val numberOfMessages = args(3).toInt
    val messageBatchSize = args(4).toInt
    val waitTimeBetweenMessageBatch = args(5).toInt
    val lingerMs = args(6)
    val batchSize = args(7)

    implicit val formats = DefaultFormats

    val props = new Properties()
    props.put("bootstrap.servers", kafkaServerURL + ":" + kafkaServerPort)
    props.put("acks", "all")
    props.put("retries", "0")
    props.put("batch.size", batchSize)
    props.put("linger.ms", lingerMs)
    props.put("buffer.memory", "33554432")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    //props.put("partitioner.class",
    //  "com.malaska.kafka.training.TrainingCustomerPartitioner")

    println("Create producer")
    val producer = new KafkaProducer[String, String](props)

    println("Start Producing")
    for (i <- 0 to numberOfMessages) {
      val messageBody = write(ExampleMessage("body:" + 1, System.currentTimeMillis()))
      val producerRecord = new ProducerRecord[String,String](topic, "key:" + i, messageBody)

      producer.send(producerRecord)

      if (i % messageBatchSize == 0) {
        println("MessageCount: " + i)
        Thread.sleep(waitTimeBetweenMessageBatch)
      }
    }
    producer.flush()
    producer.close()
  }
}


