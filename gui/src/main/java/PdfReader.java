import org.icepdf.ri.common.SwingController;
import org.icepdf.ri.common.SwingViewBuilder;

import javax.swing.*;

public class PdfReader {
	JPanel viewerComponentPanel;
	PdfReader(byte[] pdfBytes){

		SwingController controller = new SwingController();

		SwingViewBuilder factory = new SwingViewBuilder(controller);

		viewerComponentPanel = factory.buildViewerPanel();

		controller.getDocumentViewController().setAnnotationCallback(
				new org.icepdf.ri.common.MyAnnotationCallback(
						controller.getDocumentViewController()));

		controller.openDocument(pdfBytes, 0, pdfBytes.length, "", "");
	}

	public JPanel getPanel(){
		return viewerComponentPanel;
	}
}
