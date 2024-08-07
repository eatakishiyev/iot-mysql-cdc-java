package iot.cdc.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.format.Json;
import iot.cdc.dto.DebeziumPayload;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MysqlCdcService {
    private final Logger logger = LoggerFactory.getLogger(MysqlCdcService.class);

    public void start(Properties properties) throws IOException {
        KafkaProducer kafkaProducer = new KafkaProducer(properties);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

// Create the engine with this configuration ...
        try (DebeziumEngine<ChangeEvent<String, String>> engine = DebeziumEngine.create(Json.class)
                .using(properties)
                .notifying((records, committer) -> {
                    for (ChangeEvent<String, String> r : records) {
                        try {
                            DebeziumPayload debeziumPayload = objectMapper.readValue(r.value(), DebeziumPayload.class);
                            if( !isInterestedTable(debeziumPayload, properties)){
                                committer.markProcessed(r);
                                continue;
                            }
                            String payload = String.format("%s:%s;%s;%s;%s",
                                    debeziumPayload.getPayload().getAfter().get("name"),
                                    debeziumPayload.getPayload().getAfter().get("value"),
                                    debeziumPayload.getPayload().getAfter().get("measurement_point_uuid"),
                                    debeziumPayload.getPayload().getAfter().get("device_id"),
                                    debeziumPayload.getPayload().getAfter().get("value_detected_at"));
                            String topicName = properties.getProperty("topic.prefix");
                            kafkaProducer.send(new ProducerRecord(topicName, payload), (recordMetadata, e) -> {
                                if (e == null) {
                                    try {
                                        committer.markProcessed(r);
                                        logger.info("payload successfully published to kafka topic {}: {}", topicName, payload);
                                    } catch (Exception ex) {
                                        logger.info("error occurred during publish payload to kafka topic {}: {}", topicName, payload, ex);
                                    }
                                }
                            });
                        } catch (Throwable t) {
                            logger.error("error occurred during deserialize record : {}", r.value(), t);
                        }
                    }
                    committer.markBatchFinished();
                }).build()
        ) {
            // Run the engine asynchronously ...
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(engine);

            // Do something else or wait for a signal or an event
        }
    }

    private boolean isInterestedTable(DebeziumPayload debeziumPayload, Properties properties) {
        if (debeziumPayload == null) {
            return false;
        }

        if (debeziumPayload.getPayload() == null) {
            return false;
        }

        if (debeziumPayload.getPayload().getSource() == null) {
            return false;
        }

        if(debeziumPayload.getPayload().getAfter() == null){
            return false;
        }

        String db = debeziumPayload.getPayload().getSource().get("db");
        String table = debeziumPayload.getPayload().getSource().get("table");

        if (db == null || table == null) {
            return false;
        }

        return db.equalsIgnoreCase(properties.getProperty("interested.database")) && table.equalsIgnoreCase(properties.getProperty("interested.table"));
    }
}
