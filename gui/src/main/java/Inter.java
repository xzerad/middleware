import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import javax.xml.parsers.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;

public class Inter extends JFrame {

	JList<Object> pseudolist;
	JTabbedPane pseudotab;
	JSplitPane jsp;
	JLabel lb ;
	DefaultListModel<Object> model;
	ArrayList<HashMap<String, String>> pdf_s;

	Inter() {
		model = new DefaultListModel<>();
		serialize_cv();
		new Thread(new KafkaHandler()).start();
		this.setVisible(true);
		lb = new JLabel(" welcome ! ");
		lb.setOpaque(true);
		lb.setForeground(Color.blue);
		lb.setHorizontalAlignment(JLabel.CENTER);
		lb.setVerticalAlignment(JLabel.CENTER);
		lb.setFont(new Font("Arial",Font.BOLD,30));
		this.add(lb, BorderLayout.NORTH);
		pseudolist = new JList<>() ;
		pseudolist.setModel(model);
		pseudolist.setPreferredSize(new Dimension(400,200));
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
				System.out.println(i);
				if(i>=0){
					System.out.println(i);
					Object elementAt = model.getElementAt(i);
					HashMap<String, String> hashMap = pdf_s.get(i);
					String uuid = hashMap.get("uuid");
					String url = "http://127.0.0.1:5000/"+uuid;
					pseudotab.addTab((String) pseudolist.getSelectedValue(), new PdfPanel(url, hashMap, elementAt));
				}
			}
		});
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				try {
					FileOutputStream fileOutputStream = new FileOutputStream("./array.ser");
					ObjectOutputStream outputStream = new ObjectOutputStream(fileOutputStream);
					outputStream.writeObject(pdf_s);
					System.out.println("d");
					outputStream.close();
					fileOutputStream.close();
				} catch (IOException fileNotFoundException) {
					fileNotFoundException.printStackTrace();
				}
			}
		});
		this.add(jsp);
		this.setSize(1200, 900);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	private void serialize_cv(){

		FileInputStream fileIn;
		try {
			fileIn = new FileInputStream("./array.ser");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			pdf_s = (ArrayList<HashMap<String, String>>) in.readObject();
			pdf_s.forEach(hashMap -> {
				model.addElement(hashMap.get("username"));
			});
			System.out.println("array.ser found");

		} catch (IOException | ClassNotFoundException e) {
			System.out.println("array.ser not found");
			pdf_s = new ArrayList<>();
		}
	}

	public static void main(String[] args) {
		System.getProperties().put("org.icepdf.core.imageReference", "smoothScaled");
		System.getProperties().put("org.icepdf.core.screen.interpolation", "VALUE_INTERPOLATION_BICUBIC");
		new Inter();
	}

	class KafkaHandler implements Runnable{
		Consumer<String, String> consumer;

		KafkaHandler(){
			Properties properties = new Properties();
			properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
			properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
			properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
			properties.put(ConsumerConfig.GROUP_ID_CONFIG, "demo-topic-consumer-group");
			properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
			properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
			consumer = new KafkaConsumer<>(properties);
			consumer.subscribe(Collections.singletonList("moula"));
		}

		@Override
		public void run() {

			System.out.println("start");
			DocumentBuilder builder = null;
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

			try {
				builder = factory.newDocumentBuilder();

			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			}

			while (true){
				ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofMillis(10));
				DocumentBuilder finalBuilder = builder;

				consumerRecords.forEach(record ->{
					String value = record.value();
					HashMap<String, String> hash = new HashMap<>();

							try {
								Document doc = finalBuilder.parse(new ByteArrayInputStream(value.getBytes(StandardCharsets.UTF_8)));
								doc.getDocumentElement().normalize();
								NodeList nList = doc.getElementsByTagName("user");
								for (int i=0; i<nList.getLength(); i++) {
									String user = ((Element) nList.item(i)).getAttribute("name");
									model.addElement(user);
									hash.put("username", user);
									hash.put("email", ((Element) nList.item(i)).getAttribute("email"));

								}

								nList = doc.getElementsByTagName("file");
								for (int i=0; i<nList.getLength(); i++) {
									hash.put("filename", ((Element) nList.item(i)).getAttribute("name"));
									hash.put("uuid", ((Element) nList.item(i)).getAttribute("uuid"));
								}
								pdf_s.add(hash);
							} catch (SAXException | IOException e) {
								e.printStackTrace();
					}
				}
			);
		}}
	}

	public class PdfPanel extends JPanel {
		String email;
		PdfPanel(String url, HashMap<String, String> object, Object component) {
			this.email = object.get("email");
			PdfReader first = new PdfReader(url);
			JPanel p1 = first.getPanel();
			setLayout(new BorderLayout());
			add(p1, BorderLayout.CENTER);
			JPanel p2 =new JPanel();
			JButton Yes,No ;
			Yes= new JButton("Accepter ");
			JPanel panel = this;
			Yes.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					new Thread(new StateHandler("accept", "accept", object, component, panel)).start();
				}
			});


			No = new JButton("Refuser ") ;
			No.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					new Thread(new StateHandler("refuse", "refuse", object, component, panel)).start();

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
			HashMap<String, String> object;
			ProducerRecord<String, String> record;
			Object component;
			JPanel panel;

			StateHandler(String state, String topic, HashMap<String, String> object, Object component, JPanel pan) {
				this.object = object;
				this.component = component;
				panel = pan;
				properties = new Properties();
				properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
				properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
				properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
				second_producer = new KafkaProducer<String, String>(properties);
				record = new ProducerRecord<String, String>(topic,  state, email);
				System.out.println(state);
			}
			@Override
			public void run() {
//				System.out.println("hello");
				second_producer.send(record);
				second_producer.flush();
				second_producer.close();
				pdf_s.remove(object);
				model.removeElement(component);
				pseudotab.remove(panel);
			}
		}

	}

}