package eclippers.patch.editor.markers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.internal.resources.File;
import org.eclipse.core.internal.resources.Folder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.internal.core.CompilationUnit;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IDecoratorManager;

import textmarker.TextMarkerPlugin;
import textmarker.parse.ParseXMLForMarkers;

public class PackageDecoratorLightweight extends LabelProvider implements ILightweightLabelDecorator {

	final static Color color = new Color(Display.getDefault(), 0xF5, 0xE6, 0x3D);

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public void refresh() {
		// Get the Demo decorator
		PackageDecoratorLightweight demoDecorator = getDemoDecorator();
		if (demoDecorator == null) {
			return;
		} else {
			// Fire a label provider changed event to decorate the
			// resources whose image needs to be updated
			demoDecorator.fireLabelEvent(new LabelProviderChangedEvent(demoDecorator));//, resourcesToBeUpdated.toArray()));
		}
	}

	public void decorate(Object element, IDecoration decoration) {
		if (element instanceof File) {
			File el = (File) element;
			IPath res = el.getFullPath();

			if (ParseXMLForMarkers.affected.contains(res)) {
				decoration.setBackgroundColor(color);
			}
		} 
	}

	public static PackageDecoratorLightweight getDemoDecorator() {
		IDecoratorManager decoratorManager = TextMarkerPlugin.getDefault().getWorkbench().getDecoratorManager();

		if (decoratorManager.getEnabled("eclippers.patch.editor.markers.decorator1")) {
			return (PackageDecoratorLightweight) decoratorManager.getLightweightLabelDecorator("eclippers.patch.editor.markers.decorator1");
		} else {
			try {
				decoratorManager.setEnabled("eclippers.patch.editor.markers.decorator1", true);
				return (PackageDecoratorLightweight) decoratorManager.getLightweightLabelDecorator("eclippers.patch.editor.markers.decorator1");
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}

	private void fireLabelEvent(final LabelProviderChangedEvent event) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				fireLabelProviderChanged(event);
			}
		});
	}
}