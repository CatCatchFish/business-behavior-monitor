package cn.cat.monitor.sdk.push.impl;

import cn.cat.monitor.sdk.model.LogMessage;
import cn.cat.monitor.sdk.push.IPush;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

public class KafkaPush implements IPush {

    private static final Logger logger = LoggerFactory.getLogger(KafkaPush.class);
    private KafkaTemplate<String, LogMessage> kafkaTemplate;

    @Override
    public void open(String host, int port) {
        logger.info("Kafka连接配置：host:{},port{}", host, port);
        String bootstrapServers = host + ":" + port;

        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "PLAINTEXT");
        props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 30 * 1000);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        ProducerFactory<String, LogMessage> producerFactory = new DefaultKafkaProducerFactory<>(props);
        this.kafkaTemplate = new KafkaTemplate<>(producerFactory);
    }

    @Override
    public void send(LogMessage logMessage) {
        try {
            kafkaTemplate.send("business-behavior-monitor-sdk-topic", logMessage);
        } catch (Exception e) {
            logger.error("警告: 业务行为监控组件，推送日志消息失败", e);
        }
    }

}
