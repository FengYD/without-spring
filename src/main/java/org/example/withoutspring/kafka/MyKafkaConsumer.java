package org.example.withoutspring.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.streams.StreamsConfig;
import org.example.withoutspring.redis.RedisUtils;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

/**
 * @author fengyadong
 * @date 2022/6/10 17:24
 * @Description
 */
public class MyKafkaConsumer {

    private static KafkaConsumer kafkaConsumer;

    static {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "group-1");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("max.poll.records", 1000);
        props.put("auto.offset.reset", "earliest");
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());
        kafkaConsumer = new KafkaConsumer<>(props);
    }

    public static void main(String[] args) {
        kafkaConsumer.subscribe(Arrays.asList("ttt"));
        ConsumerRecords<String, String> msgList = kafkaConsumer.poll(Duration.ofSeconds(1));
        System.out.printf(msgList.toString());
    }

}
