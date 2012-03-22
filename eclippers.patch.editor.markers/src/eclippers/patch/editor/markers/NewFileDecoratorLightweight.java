package eclippers.patch.editor.markers;

import org.eclipse.core.internal.resources.Project;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IDecoratorManager;

import textmarker.TextMarkerPlugin;
import textmarker.parse.ParseXMLForMarkers;

// Class extends LabelProvider because LabelProvider 
// provides methods for getting images and text labels from objects  
public class NewFileDecoratorLightweight extends LabelProvider implements ILightweightLabelDecorator {

	private static final ImageDescriptor lockDescriptor = TextMarkerPlugin.getImageDescriptor("/icons/plus_overlay.png");
	
	public NewFileDecoratorLightweight() {
		super();
	}

	@Override
	public void decorate(Object element, IDecoration decoration) {
		IResource resfile = (IResource) element;
		IPath res = resfile.getFullPath();

		//highlight parent containers of the affected file
		for(int i = 0; i < ParseXMLForMarkers.newlyAddedFiles.size(); i++){
			IPath path = ParseXMLForMarkers.newlyAddedFiles.get(i);
			
			//check just package name
			/*if(resfile instanceof Project){
				IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
				if(((Project)resfile).getName().equals(file.getProject().getName())){
					decoration.addOverlay(lockDescriptor, IDecoration.TOP_RIGHT);
					break;
				} else {
					decoration.addOverlay(null);
				}
			} else {*/
				if(path.toString().equals(res.toString())){
					decoration.addOverlay(lockDescriptor, IDecoration.TOP_RIGHT);
					break;
				} else {
					decoration.addOverlay(null);
				}
			//}
		}
	}
	
	public void refresh() {
		// Get the Demo decorator
		NewFileDecoratorLightweight decorator = getDecorator();
		if (decorator == null) {
			return;
		} else {
			// Fire a label provider changed event to decorate the
			// resources whose image needs to be updated
			decorator.fireLabelEvent(new LabelProviderChangedEvent(decorator));
		}
	}

	public String decorateText(String label, Object object) {
		return null;
	}

	public static NewFileDecoratorLightweight getDecorator() {
		IDecoratorManager decoratorManager = TextMarkerPlugin.getDefault().getWorkbench().getDecoratorManager();

		if (decoratorManager.getEnabled("eclippers.patch.editor.markers.decorator2")) {
			return (NewFileDecoratorLightweight) decoratorManager.getLightweightLabelDecorator("eclippers.patch.editor.markers.decorator2");
		} else {
			try {
				decoratorManager.setEnabled("eclippers.patch.editor.markers.decorator2", true);
				return (NewFileDecoratorLightweight) decoratorManager.getLightweightLabelDecorator("eclippers.patch.editor.markers.decorator2");
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