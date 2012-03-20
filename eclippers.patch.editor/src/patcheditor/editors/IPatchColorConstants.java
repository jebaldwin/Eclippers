package patcheditor.editors;

import org.eclipse.swt.graphics.RGB;

public interface IPatchColorConstants {
	RGB DEFAULT = new RGB(0, 0, 0);
	RGB OFFSET = new RGB(204, 0, 0);
	RGB MINUS = new RGB(102, 51, 255);
	RGB ADD = new RGB(51, 153, 255);
	RGB DIFF = new RGB(51, 125, 51);
	RGB WHITE = new RGB(255, 255, 255);
	RGB GREEN = new RGB(193, 255, 193);
	RGB RED = new RGB(255,182,193);
	RGB BLUE = new RGB(50,50,255);
	
	//Java colors
	RGB COMMENT = new RGB(34, 139, 34);
	RGB STRING = new RGB(0, 0, 255);
	RGB KEYWORD = new RGB(193, 48, 96);
	
	//XML colors
	RGB XML_COMMENT = new RGB(128, 128, 128);
}
