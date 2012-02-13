package textmarker.add;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public class AddMarkers {

	public static void addMarkerToFile(String patchName, String fileName,
			int lineNumber, int length) {

		//allow for default context numbers
		int lineNum = lineNumber + 3;
		
		IProject[] projs = ResourcesPlugin.getWorkspace().getRoot()
				.getProjects();
		IPath path = new Path(projs[0].getName() + File.separator + fileName);
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);

		try {
			IFile iFile = file;
			IMarker marker = null;
			boolean conflict = false;

			IMarker[] markers = iFile.findMarkers(null, true,
					IResource.DEPTH_ZERO);

			for (int i = markers.length - 1; i >= 0; i--) {
				IMarker markerAtIndex = markers[i];
				try {
					int lineOfMarker = ((Integer) markerAtIndex
							.getAttribute(IMarker.LINE_NUMBER)).intValue();

					if (lineOfMarker == lineNum) {
						marker = iFile.createMarker(IMarker.PROBLEM);
						marker.setAttribute(IMarker.MESSAGE, patchName
								+ " patch and possibly an aspect apply here!");
						conflict = true;
					}
				} catch (NullPointerException npe) {

				}
			}

			if (!conflict) {
				marker = iFile.createMarker("patchAppliesMarker");
				marker.setAttribute(IMarker.MESSAGE, patchName + " patch applies here!");
			}

			marker.setAttribute("owner", patchName);
			marker.setAttribute(IMarker.LINE_NUMBER, lineNum);
			marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
			
			//set navigation to patch file
			
			//NavigateToSourceAction.openFile(patchName + ".patch");

		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	public static void clearMarkers(String fileName, String ownerName) {
		IProject[] projs = ResourcesPlugin.getWorkspace().getRoot()
				.getProjects();
		IPath path = new Path(projs[0].getName() + File.separator + fileName);
		IFile iFile = ResourcesPlugin.getWorkspace().getRoot().getFile(path);

		try {
			IMarker[] markers = iFile.findMarkers(null, true,
					IResource.DEPTH_ZERO);
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
		}
	}
}
