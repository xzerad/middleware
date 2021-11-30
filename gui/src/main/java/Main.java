import org.apache.commons.lang3.ArrayUtils;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.icepdf.ri.common.SwingController;
import org.icepdf.ri.common.SwingViewBuilder;
import org.apache.commons.io.FileUtils;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.Collections;
import java.util.Properties;

public class Main extends JFrame {
	Main() {
//		Properties properties = new Properties();
//		properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
//		properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//		properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);
//		properties.put(ConsumerConfig.GROUP_ID_CONFIG, "demo-topic-consumer-group");
//		properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
//		properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
//		Consumer<String, byte[]> consumer = new KafkaConsumer<String, byte[]>(properties);
//		consumer.subscribe(Collections.singletonList("pdf"));

		String filePath = "C:\\Users\\Radwan\\Desktop\\interfacePdf\\src\\main\\java\\Imen-Pres.pdf";
//		byte[] data = Files.readAllBytes();
//		ArrayList<byte[]> l = new ArrayList<byte[]>();


//		try {
//			data = FileUtils.readFileToByteArray(new File(filePath));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		while (true){
//			ConsumerRecords<String, byte[]> consumerRecords = consumer.poll(Duration.ofMillis(1000));
//			if (consumerRecords.count() > 0){
//				System.out.println(consumerRecords.count());
//				consumerRecords.forEach(record ->{
//					System.out.println(record.key());
////					System.out.println(record.value() instanceof byte[]);
////					byte[] b = new byte[len];
////					for(int i = 0; i < len; i++){
////						b[i] = record.value()[i];
////					}
//					l.add(record.value());
//				});
////			break;
//			}
//
//		}

//		 build a component controller
		SwingController controller = new SwingController();

		SwingViewBuilder factory = new SwingViewBuilder(controller);

		JPanel viewerComponentPanel = factory.buildViewerPanel();

		// add interactive mouse link annotation support via callback
		controller.getDocumentViewController().setAnnotationCallback(
				new org.icepdf.ri.common.MyAnnotationCallback(
						controller.getDocumentViewController()));


//		System.out.println(Arrays.toString(a));
		// Now that the GUI is all in place, we can try openning a PDF
//		data = l.get(0);
		controller.openDocument(filePath);
		JSplitPane jsp = new JSplitPane();
		jsp.setRightComponent(viewerComponentPanel);
		DefaultListModel<String> m = new DefaultListModel<>();
////
		JList<String> l = new JList<String>();

		JPanel pan = new JPanel();

		pan.setLayout(new BorderLayout());
//		pan.setPreferredSize(new Dimension(250, 0));
//		pan.add(l);
		JButton but = new JButton("sdf");
//		m.addElement(but);
		jsp.setLeftComponent(pan);
//		jsp.getLeftComponent().setSize(new Dimension(500, 500));
		add(jsp, BorderLayout.CENTER);
		setTitle("Test Evenement");
		setVisible(true);
		setSize(1350, 1000);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
	}

	public static void main(String[] args) {
		new Main();
	}
}
