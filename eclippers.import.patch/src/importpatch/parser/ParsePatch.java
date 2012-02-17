package importpatch.parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ParsePatch {

	/*private static final String XML_FROM_PATCH_START = "<patch ";

	private static final String XML_FROM_PATCH_END = "</patch>";*/

	private static final String XML_FILE = "patchData.xml";

	/*private static final String XML_DIFF_START = "<file ";

	private static final String XML_DIFF_END = "</file>";

	private static final String XML_OFFSET_START = "<offset ";

	private static final String XML_OFFSET_END = "</offset>";

	private static final String XML_ADD_LINE_START = "<addline ";

	private static final String XML_ADD_LINE_END = "</addline>";

	private static final String XML_MINUS_LINE_START = "<remline ";

	private static final String XML_MINUS_LINE_END = "</remline>";*/
	
	private static String WORKSPACE_PATH = ResourcesPlugin.getWorkspace().getRoot().getLocation().toPortableString();

	public static void parse(String pathName, String fileName, IProject proj) throws IOException {
		int index = pathName.indexOf(fileName);
		File xmlFile = new File(pathName.substring(0, index) + XML_FILE);
		if(xmlFile.exists()){
			BufferedWriter output = new BufferedWriter(new FileWriter(xmlFile));
			output.write("<globalPatchData></globalPatchData>");
			output.close();
		} else {
			xmlFile.createNewFile();
		}
		File patchFile = new File(pathName);
		parseToXML(patchFile, xmlFile, proj);
	}
/*
	private static void parseToXMLOld(File patchFile, File xmlFile, IProject proj) {

		BufferedReader input = null;
		BufferedWriter output = null;

		try {
			removeLastTag(xmlFile);

			input = new BufferedReader(new FileReader(patchFile));
			output = new BufferedWriter(new FileWriter(xmlFile, true));

			int index = patchFile.getName().indexOf('.');
			String patchName = patchFile.getName().substring(0, index);
			
			output.write(XML_FROM_PATCH_START + "name=\"" + patchName + "\">\n");
			String line = null; // not declared within while loop
			String fileName = null;
			String modSection = null;
			int addLineCount = 0;
			int lineCount = 0;

			while ((line = input.readLine()) != null) {

				if (line.startsWith("+++")) {
					// contains filename
					if (fileName != null) {
						output.write("\t\t" + XML_OFFSET_END + "\n");
						output.write("\t" + XML_DIFF_END + "\n");
					}

					String[] array = line.split("\\s");
					fileName = array[1];
					output.write("\t" + XML_DIFF_START);
					modSection = null;

					// need to get length of file
					int length = getFileLength(fileName, proj);
					String[] results = splitPath(fileName);
					String packageName = results[0];
					fileName = results[1];

					output.write("package=\"" + packageName + "\" name=\""
							+ fileName + "\" length=\"" + length + "\">\n");
				} else if (line.startsWith("@@")) {
					// contains starting line and number of lines changed
					if (modSection != null)
						output.write("\t\t" + XML_OFFSET_END + "\n");
					modSection = line;
					index = line.indexOf(',');
					int startAt = Integer.parseInt(line.substring(4, index));
					index = line.lastIndexOf('+');
					int index2 = line.lastIndexOf(',');
					int startAtSecondFile = Integer.parseInt(line.substring(index + 1, index2));
					addLineCount = startAtSecondFile;
					lineCount = 0;
					String[] array = line.split("\\s");
					String halfLine = array[2];
					index = halfLine.indexOf(',');
					String halfLine2 = array[1];
					index2 = halfLine2.indexOf(',');
					int lengthBefore = Integer.parseInt(halfLine2
							.substring(index2 + 1));
					int lengthAfter = Integer.parseInt(halfLine
							.substring(index + 1));
					output.write("\t\t" + XML_OFFSET_START);
					output.write("start=\"" + startAt + "\" ");
					output.write("length=\""
							+ (Math.abs(lengthAfter - lengthBefore)) + "\">\n");
				} else if (line.startsWith("-") && !line.startsWith("---")) {
					output.write("\t\t\t" + XML_MINUS_LINE_START + "at=\""
							+ addLineCount++ + "\"/>\n");
					lineCount++;
					// output.write("\t\t\t" + XML_MINUS_LINE_START + "at=\"" +
					// lineCount++ + "\">");
					// output.write(line.substring(1));
					// output.write(XML_MINUS_LINE_END + "\n");
				} else if (line.startsWith("+")) {
					output.write("\t\t\t" + XML_ADD_LINE_START + "at=\""
							+ (addLineCount++ - lineCount) + "\"/>\n");
					// output.write("\t\t\t" + XML_ADD_LINE_START + "at=\"" +
					// lineCount++ + "\">");
					// output.write(line.substring(1));
					// output.write(XML_ADD_LINE_END + "\n");
				} else {
					addLineCount++;
				}
			}

			output.write("\t\t" + XML_OFFSET_END + "\n");
			output.write("\t" + XML_DIFF_END + "\n");
			output.write(XML_FROM_PATCH_END + "\n");
			output.write("</globalPatchData>");
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (input != null) {
					input.close();
				}
				if (output != null) {
					output.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}*/
	
	private static void parseToXML(File patchFile, File xmlFile, IProject proj) {

		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse (xmlFile);
            Element root = doc.getDocumentElement();
            
            BufferedReader input = new BufferedReader(new FileReader(patchFile));
            String line;
			String fileName = null;
			int addLineCount = 0;
			int lineCount = 0;
			
			int index = patchFile.getName().indexOf('.');
			String patchName = patchFile.getName().substring(0, index);
			Element patchEl = doc.createElement("patch");
			patchEl.setAttribute("id", patchName);
			root.appendChild(patchEl);
			Element diffStart = null;
			
            while((line = input.readLine()) != null){
            	if (line.startsWith("+++")) {
            		//contains filename
					String[] array = line.split("\\s");
					fileName = array[1];
					diffStart = doc.createElement("file");
					
					// need to get length of file
					int length = getFileLength(fileName, proj);
					String[] results = splitPath(fileName);
					String packageName = results[0];
					fileName = results[1];

					diffStart.setAttribute("package", packageName);
					diffStart.setAttribute("name", fileName);
					diffStart.setAttribute("length", Integer.toString(length));
					patchEl.appendChild(diffStart);
				} else if (line.startsWith("@@")) {
					// contains starting line and number of lines changed				
					index = line.indexOf(',');
					int startAt = Integer.parseInt(line.substring(4, index));
					index = line.lastIndexOf('+');
					int index2 = line.lastIndexOf(',');
					int startAtSecondFile = Integer.parseInt(line.substring(index + 1, index2));
					addLineCount = startAtSecondFile;
					lineCount = 0;
					String[] array = line.split("\\s");
					String halfLine = array[2];
					index = halfLine.indexOf(',');
					String halfLine2 = array[1];
					index2 = halfLine2.indexOf(',');
					int lengthBefore = Integer.parseInt(halfLine2.substring(index2 + 1));
					int lengthAfter = Integer.parseInt(halfLine.substring(index + 1));
					
					Element off = doc.createElement("offset");
					off.setAttribute("start", Integer.toString(startAt));
					off.setAttribute("length", Integer.toString(Math.abs(lengthAfter - lengthBefore)));
					diffStart.appendChild(off);
				} else if (line.startsWith("-") && !line.startsWith("---")) {
					Element min = doc.createElement("remline");
					min.setAttribute("at", Integer.toString(addLineCount++));
					lineCount++;
					diffStart.appendChild(min);
				} else if (line.startsWith("+")) {
					Element min = doc.createElement("remline");
					min.setAttribute("at", Integer.toString(addLineCount++ - lineCount));
					lineCount++;
					diffStart.appendChild(min);
				} else {
					addLineCount++;
				}
            }
            
            //write out new xml
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new FileOutputStream(xmlFile));
            transformer.transform(source, result); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*private static void removeLastTag(File xmlFile) {
		try {
			BufferedReader input = new BufferedReader(new FileReader(xmlFile));
			String line = "";
			String results = "";

			int count = 0;
			while ((line = input.readLine()) != null) {

				if (line.indexOf("</globalPatchData>") < 0) {
					results += line + "\n";
				}
				count++;
			}

			BufferedWriter output = new BufferedWriter(new FileWriter(xmlFile));

			if (count == 0) {
				output.write("<globalPatchData>\n");
			} else {
				output.write(results);
			}

			input.close();
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/

	private static String[] splitPath(String fileName) {
		String[] array = new String[2];
		
		//Patch format for separator
		int index = fileName.lastIndexOf("/");
		String packageName = fileName.substring(0, index);
		String file = fileName.substring(index + 1);
		index = file.indexOf('.');
		file = file.substring(0, index);
		packageName = packageName.replace(File.separatorChar, '.');

		array[0] = packageName;
		array[1] = file;

		return array;
	}
	
	public static void markAsPatched(File patchFile, IProject proj){
		File xmlFile = new File(WORKSPACE_PATH + XML_FILE);
		
		//mark as patched in xml
		try{
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
	        Document doc = docBuilder.parse (xmlFile);
	        
	        int index = patchFile.getName().indexOf('.');
	        String patchName = patchFile.getName().substring(0, index);
	        Element el = doc.getElementById(patchName);
	        el.setAttribute("applied", "true");
	        
	        //write out new xml
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new FileOutputStream(xmlFile));
            transformer.transform(source, result); 
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	private static int getFileLength(String fileName, IProject proj) {
		String path = WORKSPACE_PATH + File.separator + proj.getName() + File.separator + fileName;
		File countFile = new File(path);
		int count = 0;

		BufferedReader read;
		try {
			read = new BufferedReader(new FileReader(countFile));

			while ((read.readLine()) != null) {
				count++;
			}
			count++;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return count;
	}
}
