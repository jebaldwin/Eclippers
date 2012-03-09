package visualiser.convertxml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import visualiser.PatchVisualiserPlugin;

public class ConvertXMLtoMVIS {

	private static String WORKSPACE_ROOT = ResourcesPlugin.getWorkspace().getRoot().getLocation().toPortableString();
	private static final String CONTENT_XSL = "convertXMLtoContent.xsl";
	private static final String MARKUP_XSL = "convertXMLtoMarkup.xsl";

	public static void convertContentVis(IProject proj) {

		try {
			File contentFile = new File(proj.getLocation() + File.separator + "Content.vis");
			contentFile.delete();
			contentFile.createNewFile();
			
			String xslPath = "";
			Bundle bundle = Platform.getBundle(PatchVisualiserPlugin.PLUGIN_ID);
			if (bundle != null) {
				xslPath = bundle.getLocation().replace("reference:file:/", "") + "src" + File.separator + "visualiser" + File.separator + "convertxml" + File.separator + CONTENT_XSL;
			}
			String contents = xslConvert(xslPath, proj, "");
			
			//need to remove top xml generated lines from contents
			int index = contents.indexOf("\n");
			contents = contents.substring(index + 1);
			
            FileWriter out = new FileWriter(contentFile);
            out.write(contents);
            out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void convertMarkupVis(IProject proj) {

		try {
			File markupFile = new File(proj.getLocation() + File.separator + "Markup.mvis");
			markupFile.delete();
			markupFile.createNewFile();
			
			String xslPath = "";
			Bundle bundle = Platform.getBundle(PatchVisualiserPlugin.PLUGIN_ID);
			if (bundle != null) {
				xslPath = bundle.getLocation().replace("reference:file:/", "") + "src" + File.separator + "visualiser" + File.separator + "convertxml" + File.separator + MARKUP_XSL;
			}
			String contents = xslConvert(xslPath, proj, "");
			
			//need to remove top xml generated lines from contents
			int index = contents.indexOf("\n");
			contents = contents.substring(index + 1);
			
            FileWriter out = new FileWriter(markupFile);
            out.write(contents);
            out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String xslConvert(String conversionFile, IProject proj, String pathPrefix) {

		try {
			File xslFile = new File(conversionFile);
			File xmlFile = new File(proj.getLocation() + File.separator + pathPrefix + File.separator + "patch.cfg");

			if(xmlFile.exists()){
				TransformerFactory transFact = TransformerFactory.newInstance();
	
				try {
					Transformer trans = transFact.newTransformer(new StreamSource(xslFile));
					StringWriter stringWriter = new StringWriter();
					StreamResult streamResult = new StreamResult(stringWriter);
					trans.transform(new StreamSource(xmlFile), streamResult);
					String output = stringWriter.toString();
					stringWriter.close();			
					return output;
				} catch (TransformerException e) {
					e.printStackTrace();
				} 
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "";
	}
}
