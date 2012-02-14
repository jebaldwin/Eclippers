package textmarker.parse;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import textmarker.add.AddMarkers;

public class ParseXMLForMarkers {
	
	public static String WORKSPACE_ROOT = ResourcesPlugin.getWorkspace().getRoot().getLocation().toPortableString();

	public static void parseXML(IProject proj) {

		File xmlFile = new File(WORKSPACE_ROOT + File.separator + proj.getName() + File.separator + "patchData.xml");

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
			builder = factory.newDocumentBuilder();
			Document document = builder.parse(xmlFile);
			
			//get the root element
			Element rootElement = document.getDocumentElement();

			//get each patch element
			NodeList nl = rootElement.getElementsByTagName("patch");
			if(nl != null && nl.getLength() > 0) {
				for(int i = 0 ; i < nl.getLength();i++) {
					Element patchElement = (Element)nl.item(i);
					String patchName = patchElement.getAttribute("name");
					
					NodeList n2 = patchElement.getElementsByTagName("file");
					if(n2 != null && n2.getLength() > 0) {
						for (int j = 0; j < n2.getLength(); j++) {
							Element fileElement = (Element)n2.item(j);
							String fileName = fileElement.getAttribute("name") + ".java";
							String filePath = fileElement.getAttribute("package");
							filePath = filePath.replaceAll("\\.", "/");
							String fullPath = filePath + File.separator + fileName;
							
							AddMarkers.clearMarkers(fullPath, patchName, proj);
							
							NodeList n3 = fileElement.getElementsByTagName("offset");
							if(n3 != null && n3.getLength() > 0) {
								for(int k = 0; k < n3.getLength(); k++) {
									Element offsetElement = (Element)n3.item(k);
									int lineNumber = Integer.parseInt(offsetElement.getAttribute("start"));
									int length = Integer.parseInt(offsetElement.getAttribute("length"));;

									//TODO figure out which style to use - patched or not patched
									AddMarkers.addMarkerToFile(patchName, fullPath, lineNumber, length, proj);
								}
							}
						}
					}
				}
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
