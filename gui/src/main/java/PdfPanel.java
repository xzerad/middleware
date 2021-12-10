import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

public class PdfPanel extends JPanel {
    String email;
    PdfPanel(String url, String email) {
    this.email = email;

    PdfReader first = new PdfReader(url);
    JPanel p1 = first.getPanel();
    setLayout(new BorderLayout());
    add(p1, BorderLayout.CENTER);
    JPanel p2 =new JPanel();
        JButton Yes,No ;
        Yes= new JButton("Accepter ");

        Yes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new StateHandler("accept")).start();

            }
        });



        No = new JButton("Refuser ") ;
        No.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new StateHandler("refuse")).start();

            }
        });


        p2.add(Yes);
        p2.add(No);
        p2.setBorder(new EmptyBorder(0,0,15,0));
        add(p2, BorderLayout.SOUTH);


}




    class StateHandler implements Runnable{
        KafkaProducer<String,String> second_producer;
        Properties properties;
        ProducerRecord<String, String> record;
        StateHandler(String state) {
            properties = new Properties();
            properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
            properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            second_producer = new KafkaProducer<String, String>(properties);
            record = new ProducerRecord<String, String>("TopicName2",  state, email);
            System.out.println(state);
        }
        @Override
        public void run() {
            second_producer.send(record);
            second_producer.flush();
            second_producer.close();
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.add(new PdfPanel("", ""));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
