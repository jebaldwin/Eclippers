package textmarker.actions;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;

public class SaveFileWithAnnotations implements IResourceChangeListener {
	
	static ArrayList<IFile> changed = new ArrayList<IFile>();
	
	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		if (event.getType() != IResourceChangeEvent.POST_CHANGE)
            return;
		
		System.out.println("event!");
        IResourceDelta rootDelta = event.getDelta();
         
        // changed = new ArrayList<IFile>();
         
         IResourceDeltaVisitor visitor = new IResourceDeltaVisitor() {
            public boolean visit(IResourceDelta delta) {
               //only interested in changed resources (not added or removed)
               if (delta.getKind() != IResourceDelta.CHANGED)
                  return true;
               //only interested in content changes
               if ((delta.getFlags() & IResourceDelta.CONTENT) == 0)
                  return true;
               IResource resource = delta.getResource();
               //only interested in files with the "txt" extension
               if (resource.getType() == IResource.FILE) {         	
            	   System.out.println(changed.size());
            	  if(!changed.contains(resource) 
            			  && !resource.getName().endsWith(".class") 
            			  && !resource.getName().endsWith(".jar") ){
            		  System.out.println(resource.getName());
            		  changed.add((IFile)resource);
            	  }
               }
               return true;
            }
         };
         try {
             rootDelta.accept(visitor);
          } catch (CoreException e) {
             //open error dialog with syncExec or print to plugin log file
          }
          //nothing more to do if there were no changed text files
          if (changed.size() == 0)
             return;
          else 
        	 changed = new ArrayList<IFile>();
	}

}
