package org.example.withoutspring.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.StreamsConfig;

import java.util.Properties;

/**
 * @author fengyadong
 * @date 2022/6/10 17:31
 * @Description
 */
public class MyKafkaProducer {

    private static KafkaProducer<String, String> kafkaProducer;

    static {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("key.serializer", StringSerializer.class.getName());
        props.put("value.serializer", StringSerializer.class.getName());

        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        kafkaProducer = new KafkaProducer(props);
    }

    public static void main(String[] args) {
        kafkaProducer.send(new ProducerRecord("hotitems", "bbb"));
        kafkaProducer.send(new ProducerRecord("hotitems", "ccc"));
    }

}
