package com.ucl.spark.demo;

import kafka.security.auth.ResourceType;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.SparkConf;
import org.apache.spark.TaskContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.*;
import org.spark_project.jetty.util.resource.ResourceFactory;
import scala.Tuple2;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SparkStream {
        public static void main(String[] args) {
                Map<String, Object> kafkaParams = new HashMap<>();
                kafkaParams.put("bootstrap.servers", "10.1.210.50:9092");
                kafkaParams.put("key.deserializer", StringDeserializer.class);
                kafkaParams.put("value.deserializer", StringDeserializer.class);
                kafkaParams.put("group.id", "use_a_separate_group_id_for_each_stream");
                kafkaParams.put("auto.offset.reset", "latest");
                kafkaParams.put("enable.auto.commit", false);

                Collection<String> topics = Arrays.asList("replication-threadA", "replication-threadB");

//                SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("NetworkWordCount");
                SparkConf conf = new SparkConf().setMaster("spark://10.1.210.50:7077").setAppName("NetworkWordCount");
                JavaStreamingContext streamingContext = new JavaStreamingContext(conf, Durations.seconds(1));

                JavaInputDStream<ConsumerRecord<String, String>> stream =
                        KafkaUtils.createDirectStream(
                                streamingContext,
                                LocationStrategies.PreferConsistent(),
//                                LocationStrategies.PreferBrokers(),
                                ConsumerStrategies.<String, String>Subscribe(topics, kafkaParams));

                stream.mapToPair(record -> new Tuple2<>(record.key(), record.value()));

                stream.foreachRDD(rdd -> {
                        rdd.foreachPartition(consumerRecord -> {
//                                OffsetRange o = offsetRanges[TaskContext.get().partitionId()];
                                System.out.println("partitionId: " + TaskContext.get().partitionId());
                        });
                });
        }
}