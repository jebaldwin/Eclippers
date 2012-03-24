package textmarker.actions;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IExecutionListener;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class SaveAnnotatedFile implements IExecutionListener {

	@Override
	public void notHandled(String commandId, NotHandledException exception) {
	}

	@Override
	public void postExecuteFailure(String commandId, ExecutionException exception) {
	}

	@Override
	public void postExecuteSuccess(String commandId, Object returnValue) {
	}

	@Override
	public void preExecute(String commandId, ExecutionEvent event) {
		Event evt = (Event)event.getTrigger();
		
		
		Command cmd = event.getCommand();
		
		
		if(commandId.equals("org.eclipse.ui.file.save")){
			//get current editor
			IEditorPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
			if(part instanceof IFileEditorInput){
				IFile file = ((IFileEditorInput) part).getFile();
				try {
					IMarker[] markers = file.findMarkers("patchLinesMarker", false, 0);
					
					for (int i = 0; i < markers.length; i++) {
						IMarker marker = markers[i];
						
					}
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else if(commandId.equals("org.eclipse.ui.file.saveAll")){
			//get all open editors
			IEditorReference[] refs = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditorReferences();	
			for (int i = 0; i < refs.length; i++) {
				IEditorReference ref = refs[i];
				try {
					IEditorInput ei = ref.getEditorInput();
					if(ei instanceof IFileEditorInput){
						IFile file = ((IFileEditorInput) ei).getFile();
					}
				} catch (PartInitException e) {
					e.printStackTrace();
				}
			}
		}
		
	}



}
