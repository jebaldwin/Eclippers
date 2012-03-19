package eclippers.patch.editor.markers;

import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;

import textmarker.TextMarkerPlugin;


public class OverlayImageIcon extends CompositeImageDescriptor {

	private Image baseImage_;
	private Point sizeOfImage_;
	private static final ImageDescriptor lockDescriptor = TextMarkerPlugin.getImageDescriptor("/icons/plus.gif");//ImageDescriptor.createFromFile(DemoDecorator.class, "lock.gif");

	private static final int TOP_LEFT = 0;
	private static final int TOP_RIGHT = 1;
	private static final int BOTTOM_LEFT = 2;
	private static final int BOTTOM_RIGHT = 3;

	public OverlayImageIcon(Image baseImage) {
		baseImage_ = baseImage;
		sizeOfImage_ = new Point(baseImage.getBounds().width, baseImage.getBounds().height);
	}

	protected void drawCompositeImage(int arg0, int arg1) {
		// Draw the base image
		drawImage(baseImage_.getImageData(), 0, 0);
		//int[] locations = organizeImages();
		//for (int i = 0; i < imageKey_.size(); i++) {
			//ImageData imageData = lockDescriptor.getImageData((String) imageKey_.get(i));
		ImageData imageData = lockDescriptor.getImageData();
			//switch (locations[i]) {
			// Draw on the top left corner
			//case TOP_LEFT:
			//	drawImage(imageData, 0, 0);
			//	break;

			// Draw on top right corner
			//case TOP_RIGHT:
				drawImage(imageData, sizeOfImage_.x - imageData.width, 0);
				//break;

			// Draw on bottom left
			//case BOTTOM_LEFT:
			//	drawImage(imageData, 0, sizeOfImage_.y - imageData.height);
			//	break;

			// Draw on bottom right corner
			//case BOTTOM_RIGHT:
			//	drawImage(imageData, sizeOfImage_.x - imageData.width, sizeOfImage_.y - imageData.height);
			//	break;

			//}
		//}
	}

	protected Point getSize() {
		return sizeOfImage_;
	}

	public Image getImage() {
		return createImage();
	}

}
