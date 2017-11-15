package com.ucl.spark.stream;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jiang.zheng on 2017/11/15.
 */
public class SparkStreamModel {
    public static void main(String[] args) {
        Map<String, Object> kafkaParams = new HashMap<>();
        kafkaParams.put("bootstrap.servers", "10.1.210.50:9092");
        kafkaParams.put("key.deserializer", StringDeserializer.class);
        kafkaParams.put("value.deserializer", StringDeserializer.class);
        kafkaParams.put("group.id", "use_a_separate_group_id_for_each_stream");
        kafkaParams.put("auto.offset.reset", "latest");
        kafkaParams.put("enable.auto.commit", false);

        SparkConf sparkConf = new SparkConf().setAppName("main.java.computingCenter").setMaster("local[2]");
        // Create the context with 2 seconds batch size
        //每两秒读取一次kafka
        JavaStreamingContext jssc = new JavaStreamingContext(sparkConf, Durations.seconds(10));

        Collection<String> topics = Arrays.asList("replication-threadA", "replication-threadB");

        JavaInputDStream<ConsumerRecord<String, String>> stream =
                KafkaUtils.createDirectStream(
                        jssc,
                        LocationStrategies.PreferConsistent(),
                        ConsumerStrategies.<String, String>Subscribe(topics, kafkaParams)
                );

        JavaDStream<String> lines = stream.map(record -> {return record.value();});
        lines.foreachRDD(o -> {
            System.out.println("rdd:" + o.count());
        });
        System.out.println("lines: " + lines);
        jssc.start();
    }
}