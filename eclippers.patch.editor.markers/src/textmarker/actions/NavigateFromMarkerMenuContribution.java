package textmarker.actions;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.source.IVerticalRulerInfo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.internal.util.BundleUtility;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;
import org.osgi.framework.Bundle;

public class NavigateFromMarkerMenuContribution extends ContributionItem {

	private ITextEditor editor;
	private IVerticalRulerInfo rulerInfo;
	private List<IMarker> markers;

	public NavigateFromMarkerMenuContribution(ITextEditor editor) {
		this.editor = editor;
		this.rulerInfo = getRulerInfo();
		this.markers = getMarkers();
	}

	private IVerticalRulerInfo getRulerInfo() {
		return (IVerticalRulerInfo) editor.getAdapter(IVerticalRulerInfo.class);
	}

	private List<IMarker> getMarkers() {
		List<IMarker> clickedOnMarkers = new ArrayList<IMarker>();
		for (IMarker marker : getAllMarkers()) {
			if (markerHasBeenClicked(marker)) {
				clickedOnMarkers.add(marker);
			}
		}

		return clickedOnMarkers;
	}

	private boolean markerHasBeenClicked(IMarker marker) {
		return (marker.getAttribute(IMarker.LINE_NUMBER, 0)) == (rulerInfo.getLineOfLastMouseButtonActivity() + 1);
	}

	private IMarker[] getAllMarkers() {
		try {
			IMarker[] array1 = ((FileEditorInput) editor.getEditorInput()).getFile().findMarkers("patchAppliesMarker", true, IResource.DEPTH_ZERO);
			IMarker[] array2 = ((FileEditorInput) editor.getEditorInput()).getFile().findMarkers("patchLinesRemovedMarker", true, IResource.DEPTH_ZERO);
			IMarker[] array3 = ((FileEditorInput) editor.getEditorInput()).getFile().findMarkers("patchLinesMarker", true, IResource.DEPTH_ZERO);
			
			IMarker[] newarray = new IMarker[array1.length + array2.length + array3.length];
			System.arraycopy(array1, 0, newarray, 0, array1.length);
			System.arraycopy(array2, 0, newarray, array1.length, array2.length);
			System.arraycopy(array3, 0, newarray, array2.length, array3.length);
			
			return newarray;
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void fill(Menu menu, int index) {
		for (final IMarker marker : markers) {
			MenuItem menuItem = new MenuItem(menu, SWT.CHECK, index);
			Bundle bundle = Platform.getBundle("eclippers.patch.editor.markers");
			URL fullPathString = BundleUtility.find(bundle, "icons/nav.gif");
			ImageDescriptor icon = ImageDescriptor.createFromURL(fullPathString);
			menuItem.setImage(icon.createImage());
			menuItem.setText("Navigate to Patch: " + marker.getAttribute("name", ""));
			menuItem.addSelectionListener(createDynamicSelectionListener(marker));
		}
	}

	private SelectionAdapter createDynamicSelectionListener(final IMarker marker) {
		return new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					if(marker.getAttribute("patched", true)){
						NavigateToSourceAction.openFile(marker.getAttribute("name", "") + ".patched", (IProject) marker.getAttribute("project"), marker.getAttribute("patchLine", 0));	
					} else {
						NavigateToSourceAction.openFile(marker.getAttribute("name", "") + ".patch", (IProject) marker.getAttribute("project"), marker.getAttribute("patchLine", 0));
					}
				} catch (CoreException ce) {
					ce.printStackTrace();
				}
			}
		};
	}
}
