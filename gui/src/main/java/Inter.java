import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.Duration;
import java.util.*;
import java.util.regex.Pattern;

public class Inter extends JFrame {

	JList<Object> pseudolist;
	JTabbedPane pseudotab;
	JSplitPane jsp;
	JLabel lb,lb1 ;
	DefaultListModel<Object> model;
	ArrayList<HashMap<String, byte[]>> pdf_s;


	Inter() {
		pdf_s = new ArrayList<>();
		new Thread(new KafkaHandler()).start();

		this.setVisible(true);
		lb = new JLabel(" welcome ! ");
		lb.setOpaque(true);
		lb.setForeground(Color.blue);
		lb.setHorizontalAlignment(JLabel.CENTER);
		lb.setVerticalAlignment(JLabel.CENTER);
		lb.setFont(new Font("Arial",Font.BOLD,30));
		this.add(lb, BorderLayout.NORTH);
		model = new DefaultListModel<>();
		pseudolist = new JList<>() ;
		pseudolist.setModel(model);
		pseudolist.setPreferredSize(new Dimension(400,400));
		pseudotab=new JTabbedPane();
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.getViewport().setView(pseudolist);
		jsp=new JSplitPane();
		jsp.setLeftComponent(scrollPane);
		jsp.setRightComponent(pseudotab);
		jsp.setBorder(new EmptyBorder(20, 10, 30, 10));
		pseudolist.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				int i = pseudolist.locationToIndex(e.getPoint());
				if(i>0){
					System.out.println(i);
					byte[] data = pdf_s.get(i-1).get("pdf");
					pseudotab.addTab((String) pseudolist.getSelectedValue(), new PdfReader(data).getPanel());
				}

			}
		});
		this.add(jsp);
		this.setSize(1900, 900);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);

	}
	public static void main(String[] args) {
		new Inter();
	}

	class KafkaHandler implements Runnable{
		Consumer<String, byte[]> consumer;

		KafkaHandler(){
			Properties properties = new Properties();
			properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
			properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
			properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);
			properties.put(ConsumerConfig.GROUP_ID_CONFIG, "demo-topic-consumer-group");
			properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
			properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
			consumer = new KafkaConsumer<>(properties);
			consumer.subscribe(Collections.singletonList("delta"));
		}
		@Override
		public void run() {
			System.out.println("start");
			while (true){
				ConsumerRecords<String, byte[]> consumerRecords = consumer.poll(Duration.ofMillis(10));
				consumerRecords.forEach(record ->{
				String key = record.key();
				System.out.println(key);
				if(key.contains("|")){
					String user;
					String mail;
					HashMap<String, byte[]> hash = new HashMap<>();
					int i = key.indexOf('|');
					user = key.substring(0, i);
					System.out.println(user);
					mail = key.substring(i+1);
					hash.put("user", user.getBytes());
					hash.put("email", mail.getBytes());
					hash.put("pdf", record.value());
					model.addElement(user);
					pdf_s.add(hash);
				}
			}
			);
//			System.out.println("end");
		}}
	}
}

