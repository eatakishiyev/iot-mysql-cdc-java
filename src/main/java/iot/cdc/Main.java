package iot.cdc;

import iot.cdc.service.MysqlCdcService;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Main {
    public static void main(String[] args) throws IOException {
        final Properties props = new Properties();
        props.load(new FileInputStream("./conf/application.properties"));
//        props.setProperty("name", "engine");
//        props.setProperty("snapshot.mode", "never");
//        props.setProperty("connector.class", "io.debezium.connector.mysql.MySqlConnector");
//        props.setProperty("offset.storage", "org.apache.kafka.connect.storage.FileOffsetBackingStore");
//        props.setProperty("offset.storage.file.filename", "./data/offsets.dat");
//        props.setProperty("offset.flush.interval.ms", "0");
//        props.setProperty("database.hostname", "localhost");
//        props.setProperty("database.port", "3306");
//        props.setProperty("database.user", "root");
//        props.setProperty("database.password", "Aa123456");
//        props.setProperty("database.server.id", "1");
//        props.setProperty("topic.prefix", "valOfDevice");
//        props.setProperty("schema.history.internal", "io.debezium.storage.file.history.FileSchemaHistory");
//        props.setProperty("schema.history.internal.file.filename", "./schemahistory.dat");
//        props.setProperty("bootstrap.servers", "192.168.100.5:9092");
//        props.setProperty("compression.type", "zstd");
//        props.setProperty("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
//        props.setProperty("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
//        props.setProperty("interested.database", "cdc-test-db");
//        props.setProperty("interested.table", "valueofdevice");
        new MysqlCdcService().start(props);
    }
}
