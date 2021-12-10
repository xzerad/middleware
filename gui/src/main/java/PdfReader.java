import org.icepdf.ri.common.SwingController;
import org.icepdf.ri.common.SwingViewBuilder;

import javax.swing.*;
import java.net.MalformedURLException;
import java.net.URL;

public class PdfReader {
	JPanel viewerComponentPanel;
	PdfReader(String url){

		SwingController controller = new SwingController();

		SwingViewBuilder factory = new SwingViewBuilder(controller);

		viewerComponentPanel = factory.buildViewerPanel();
		controller.getDocumentViewController().setAnnotationCallback(
				new org.icepdf.ri.common.MyAnnotationCallback(
						controller.getDocumentViewController()));

		try {
			controller.openDocument(new URL(url));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public JPanel getPanel(){
		return viewerComponentPanel;
	}
}
