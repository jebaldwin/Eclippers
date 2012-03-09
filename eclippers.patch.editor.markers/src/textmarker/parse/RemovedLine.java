package textmarker.parse;

import java.io.File;

public class RemovedLine{
	
	public int lineNumber;
	public int newLine;
	public int originalLine;
	public String codeLine;
	public int patchLine;
	public File checkFile;
	
	public RemovedLine(int lineNumber, int newLine, int originalLine, String codeLine, int patchLine, File checkFile) {
		this.lineNumber = lineNumber;
		this.newLine = newLine;
		this.originalLine = originalLine;
		this.codeLine = codeLine;
		this.patchLine = patchLine;
		this.checkFile = checkFile;
	}
			
}