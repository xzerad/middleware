import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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


        consumer.subscribe(Arrays.asList("topic-accepte","topic-refuse"));



            ConsumerRecords<String,String> records = consumer.poll(Duration.ofMillis(1000));
            Mongo mongo = new Mongo();
            for (ConsumerRecord record:records){

                if(record.topic()=="topic-accepte"){
                    String msg="vous etes accepté";
                    String valeur=record.value().toString();
                    System.out.println(valeur);
                    SendMail m = new SendMail(msg,valeur);
                    m.send();
                    LocalDateTime time = LocalDateTime.now();

                    mongo.update(valeur,"accepte",time.toString());
                }else if(record.topic()=="topic-refuse"){
                    String msg="vous etes refuse";
                    String valeur= record.value().toString();
                    System.out.println(valeur);
                    SendMail m = new SendMail(msg, valeur);
                    m.send();
                    LocalDateTime time = LocalDateTime.now();
                    mongo.update(valeur,"refusé",time.toString());
                }
            }




    }
}
