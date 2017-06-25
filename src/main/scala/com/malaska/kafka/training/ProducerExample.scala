package com.malaska.kafka.training

import java.util
import java.util.Properties

import org.apache.kafka.clients.producer._
import org.apache.kafka.common.Cluster

/**
  * Created by tmalaska on 6/24/17.
  */
object ProducerExample {


  def main(args:Array[String]): Unit = {
    val kafkaServerURL = args(0)
    val kafkaServerPort = args(1)
    val topic = args(2)
    val isAsync = args(3)
    val numberOfMessages = args(4).toInt
    val messageBatchSize = args(5).toInt
    val waitTimeBetweenMessageBatch = args(6).toInt
    val callbackMessageInterval = args(7).toInt

    val props = new Properties()
    props.put("bootstrap.servers", kafkaServerURL + ":" + kafkaServerPort)
    props.put("acks", "all")
    props.put("retries", "0")
    props.put("batch.size", "16384")
    props.put("linger.ms", "1")
    props.put("buffer.memory", "33554432")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    //props.put("partitioner.class",
    //  "com.malaska.kafka.training.TrainingCustomerPartitioner")
    val producer = new KafkaProducer[String, String](props)


    for (i <- 0 to numberOfMessages) {
      val producerRecord = new ProducerRecord[String,String](topic, "key:" + i, "value:" + i)
      if (i % callbackMessageInterval == 0) {
        producer.send(producerRecord, new TrainingCallback(producerRecord))
      } else {
        producer.send(producerRecord)
      }
      if (i % messageBatchSize == 0) {
        println("MessageCount: " + i)
        Thread.sleep(waitTimeBetweenMessageBatch)
      }
    }

    producer.flush()
    producer.close()
  }
}

class TrainingCallback(producerRecord: ProducerRecord[String, String]) extends Callback {
  override def onCompletion(recordMetadata: RecordMetadata, e: Exception): Unit = {
    if (recordMetadata != null) {
      val partitionNum = recordMetadata.partition()
      val keySize = recordMetadata.serializedKeySize()
      val valueSize = recordMetadata.serializedValueSize()
      val offset = recordMetadata.offset()

      println("Callback:{\"partitionNum\"=" + partitionNum +
        ",\"keySize\"=" + keySize +
        ",\"valueSize\"=" + valueSize +
        ",\"offset\"=" + offset +
        ",\"key\"=" + producerRecord.key +
        ",\"value\"=" + producerRecord.value + "}")
    }
  }
}

class TrainingCustomerPartitioner extends Partitioner {
  override def close(): Unit = {

  }

  override def partition(topic: String,
                         o: Any,
                         keyBytes: Array[Byte],
                         o1: Any,
                         valueBytes: Array[Byte],
                         cluster: Cluster): Int = {
    val partitions = cluster.partitionsForTopic(topic);
    val key = new String(keyBytes)

    Math.abs(key.hashCode % partitions.size())
  }

  override def configure(map: util.Map[String, _]): Unit = {

  }
}
