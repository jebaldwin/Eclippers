package textmarker.add;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import textmarker.actions.NavigateToSourceAction;
import textmarker.parse.ParseXMLForMarkers;

public class AddMarkers {

	public static void addMarkerToFile(String patchName, String fileName, int lineNum, IProject proj, String code, boolean applied, boolean lineAdded) {

		//allow for default context numbers
		if(!applied)
			lineNum = lineNum + 3;
				
		int index = fileName.indexOf(proj.getName());
		IPath path = new Path(fileName.substring(index));
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		File javaFile = new File(ParseXMLForMarkers.WORKSPACE_ROOT + file.getFullPath().toPortableString());

		try {
			IMarker marker = null;
			boolean conflict = false;
			
			//deprecated: look for conflicts with other markers (e.g. AspectJ)
			/*IMarker[] markers = iFile.findMarkers(null, true, IResource.DEPTH_ZERO);
			
			for (int i = markers.length - 1; i >= 0; i--) {
				IMarker markerAtIndex = markers[i];
				try {
					int lineOfMarker = ((Integer) markerAtIndex.getAttribute(IMarker.LINE_NUMBER)).intValue();

					
					if (lineOfMarker == lineNum) {
						marker = iFile.createMarker(IMarker.PROBLEM);
						marker.setAttribute(IMarker.MESSAGE, patchName + " patch and possibly an aspect apply here!");
						conflict = true;
					}
				} catch (NullPointerException npe) {

				}
			}*/

			if (!conflict) {
				
				if(!applied){
					marker = file.createMarker("patchAppliesMarker");
					marker.setAttribute(IMarker.MESSAGE, patchName + " patch will apply here. \r" + code);
				} else {
					if(lineAdded){
						marker = file.createMarker("patchLinesMarker");
						marker.setAttribute(IMarker.MESSAGE, patchName + " patch has applied here. Line added.");
						marker.setAttribute(IMarker.CHAR_START, getCharStart(lineNum - 1, javaFile));
						marker.setAttribute(IMarker.CHAR_END, getCharStart(lineNum, javaFile));
					} else {
						marker = file.createMarker("patchLinesRemovedMarker");
						marker.setAttribute(IMarker.MESSAGE, patchName + " patch has applied here. Line removed.");
						marker.setAttribute(IMarker.CHAR_START, getCharStart(lineNum - 1, javaFile));
						marker.setAttribute(IMarker.CHAR_END, getCharStart(lineNum, javaFile));						
					}
				}
			}

			marker.setAttribute(IMarker.LINE_NUMBER, lineNum);
			marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
			
			//set navigation to patch file
			//NavigateToSourceAction.openFile(patchName + ".patch", proj);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	public static void clearMarkers(String fileName, String ownerName, IProject proj) {

		int index = fileName.indexOf(proj.getName());
		IPath path = new Path(fileName.substring(index));
		IFile iFile = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		
		try {
			iFile.deleteMarkers("patchAppliesMarker", true, IResource.DEPTH_ZERO);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		try {
			iFile.deleteMarkers("patchLinesMarker", true, IResource.DEPTH_ZERO);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		try {
			iFile.deleteMarkers("patchLinesRemovedMarker", true, IResource.DEPTH_ZERO);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	private static int getCharStart(int lineNum, File javaFile) {

		try {
			BufferedReader read = new BufferedReader(new FileReader(javaFile));
			int lineNumber = 0;
			String line = "";
			int charPos = 0;
			
			try {
				while((line = read.readLine()) != null) {
					if(lineNum == lineNumber) {
						break;
					}
					
					charPos += line.length() + 2; //2 for carriage return
					lineNumber++;
				}
				
				return charPos;
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return 0;
	}
}
