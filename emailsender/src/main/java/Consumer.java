import org.apache.commons.mail.EmailException;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

public class Consumer {

    public static void main(String[] args) throws EmailException {

        final String bootstrapServers = "127.0.0.1:9092";
        final String consumerGroupID = "java-group-consumer";

        Properties p = new Properties();
        p.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapServers);
        p.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        p.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class.getName());
        p.setProperty(ConsumerConfig.GROUP_ID_CONFIG,consumerGroupID);
        p.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");

        final KafkaConsumer<String,String> consumer = new KafkaConsumer<String, String>(p);

        consumer.subscribe(Arrays.asList("accept", "refuse"));
        Mongo mongo = new Mongo();
        SimpleDateFormat DateFor = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z");

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
            for (ConsumerRecord record : records) {
                Date date = new Date();
                String time = DateFor.format(date);
                System.out.println(time);
                if (record.topic().equals("accept")) {
                    String msg = "vous etes accepté";
                    String valeur = record.value().toString();
                    System.out.println(valeur);
                    SendMail m = new SendMail(msg, valeur);
                    m.send();
                    mongo.update(valeur, "accepte", time);
                } else if (record.topic().equals("refuse")) {
                    String msg = "vous etes refuse";
                    String valeur = record.value().toString();
                    System.out.println(valeur);
                    SendMail m = new SendMail(msg, valeur);
                    m.send();
                    mongo.update(valeur, "refusé", time);
                }
            }
        }
    }
}
