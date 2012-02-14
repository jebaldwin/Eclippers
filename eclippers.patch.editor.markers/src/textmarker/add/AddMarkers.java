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

	public static void addMarkerToFile(String patchName, String fileName, int lineNumber, int length, IProject proj) {

		//allow for default context numbers
		int lineNum = lineNumber + 3;
				
		IPath path = new Path(proj.getName() + File.separator + fileName);
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);

		try {
			IFile iFile = file;
			IMarker marker = null;
			boolean conflict = false;
			
			//TODO look for conflicts with other markers (e.g. AspectJ)
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
				marker = iFile.createMarker("patchAppliesMarker");
				marker.setAttribute(IMarker.MESSAGE, patchName + " patch applies here!");
			}

			marker.setAttribute("owner", patchName);
			marker.setAttribute(IMarker.LINE_NUMBER, lineNum);
			marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
			
			//set navigation to patch file
			//NavigateToSourceAction.openFile(patchName + ".patch", proj);

		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	public static void clearMarkers(String fileName, String ownerName, IProject proj) {
		IPath path = new Path(proj.getName() + File.separator + fileName);
		IFile iFile = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		/*try {
			iFile.deleteMarkers(null, true, IResource.DEPTH_ZERO);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		try {
			IMarker[] markers = iFile.findMarkers(null, true, IResource.DEPTH_ZERO);
			IMarker[] deleteMarkers = new IMarker[markers.length];
			int deleteindex = 0;
			Object owner;
			for (int i = markers.length - 1; i >= 0; i--) {
				IMarker marker = markers[i];
				owner = marker.getAttribute("owner");

				if (owner != null && owner instanceof String)
					if (owner.equals(ownerName))
						deleteMarkers[deleteindex++] = markers[i];
			}
			if (deleteindex > 0) {
				IMarker[] todelete = new IMarker[deleteindex];
				System.arraycopy(deleteMarkers, 0, todelete, 0, deleteindex);
				iFile.getWorkspace().deleteMarkers(todelete);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	public static void addLineToFile(String patchName, String fileName, int lineNum, IProject proj) {

		IPath path = new Path(proj.getName() + "\\" + fileName);
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		File javaFile = new File(ParseXMLForMarkers.WORKSPACE_ROOT + file.getFullPath().toPortableString());

		try {
			IFile iFile = file;
			IMarker marker = null;
			boolean conflict = false;

			//TODO AspectJ Markers
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
				marker = iFile.createMarker("patchLinesMarker");
				marker.setAttribute(IMarker.MESSAGE, patchName + " patch has applied here!");
			}

			marker.setAttribute("owner", patchName);
			marker.setAttribute(IMarker.LINE_NUMBER, lineNum);
			marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
			marker.setAttribute(IMarker.CHAR_START, getCharStart(lineNum - 1, javaFile));
			marker.setAttribute(IMarker.CHAR_END, getCharStart(lineNum, javaFile));

			// set navigation to patch file
			// NavigateToSourceAction.openFile(patchName + ".patch");

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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return 0;
	}
}
